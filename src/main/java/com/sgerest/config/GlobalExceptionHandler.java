package com.sgerest.config;

import java.util.List;
import java.util.Locale;
import java.util.stream.Collectors;

import jakarta.validation.ConstraintViolationException;

import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.springframework.web.servlet.NoHandlerFoundException;
import org.springframework.web.servlet.resource.NoResourceFoundException;

import lombok.extern.log4j.Log4j2;

import com.sgerest.exception.ApiErrorResponse;
import com.sgerest.exception.ArgumentNotFoundException;
import com.sgerest.exception.TituloAlreadyExistsException;

@ControllerAdvice
@Log4j2
public class GlobalExceptionHandler {

	@ExceptionHandler(TituloAlreadyExistsException.class)
	public ResponseEntity<ApiErrorResponse> handleTituloAlreadyExists(
			TituloAlreadyExistsException ex, WebRequest request) {
		log.warn("Titulo already exists exception: {}", ex.getMessage());
		return buildResponse(HttpStatus.CONFLICT, "Conflict", ex.getMessage(), request);
	}

	@ExceptionHandler(ArgumentNotFoundException.class)
	public ResponseEntity<ApiErrorResponse> handleArgumentNotFoundException(
			ArgumentNotFoundException ex, WebRequest request) {
		log.warn("Argument not found exception: {}", ex.getMessage());
		return buildResponse(HttpStatus.NOT_FOUND, "Not Found", ex.getMessage(), request);
	}

	@ExceptionHandler(IllegalArgumentException.class)
	public ResponseEntity<ApiErrorResponse> handleIllegalArgumentException(
			IllegalArgumentException ex, WebRequest request) {
		log.warn("Illegal argument exception: {}", ex.getMessage());
		return buildResponse(HttpStatus.BAD_REQUEST, "Bad Request", ex.getMessage(), request);
	}

	@ExceptionHandler(DataIntegrityViolationException.class)
	public ResponseEntity<ApiErrorResponse> handleDataIntegrityViolation(
			DataIntegrityViolationException ex, WebRequest request) {
		log.warn("Data integrity violation", ex);

		String message = "Violacao de integridade no banco de dados";
		String constraintName = resolveConstraintName(ex);
		if (constraintName != null && isUniqueConstraint(constraintName)) {
			message = "Este valor ja esta registrado no banco de dados";
		}
		if (constraintName == null && containsUniqueKeyword(ex.getMessage())) {
			message = "Este valor ja esta registrado no banco de dados";
		}

		return buildResponse(HttpStatus.CONFLICT, "Conflict", message, request);
	}

	@ExceptionHandler(MethodArgumentNotValidException.class)
	public ResponseEntity<ApiErrorResponse> handleMethodArgumentNotValid(
			MethodArgumentNotValidException ex, WebRequest request) {
		log.warn("Validation error", ex);

		String message = buildFieldErrorsMessage(ex);
		return buildResponse(HttpStatus.BAD_REQUEST, "Validation Error", message, request);
	}

	@ExceptionHandler(ConstraintViolationException.class)
	public ResponseEntity<ApiErrorResponse> handleConstraintViolation(
			ConstraintViolationException ex, WebRequest request) {
		log.warn("Constraint violation", ex);

		String message = ex.getConstraintViolations().stream()
				.map(violation -> violation.getPropertyPath() + ": " + violation.getMessage())
				.collect(Collectors.joining("; "));
		if (message.isBlank()) {
			message = "Erro na validacao dos dados";
		}

		return buildResponse(HttpStatus.BAD_REQUEST, "Validation Error", message, request);
	}

	@ExceptionHandler(HttpMessageNotReadableException.class)
	public ResponseEntity<ApiErrorResponse> handleHttpMessageNotReadable(
			HttpMessageNotReadableException ex, WebRequest request) {
		log.warn("Invalid JSON payload", ex);
		return buildResponse(HttpStatus.BAD_REQUEST, "Bad Request", "JSON invalido", request);
	}

	@ExceptionHandler(MethodArgumentTypeMismatchException.class)
	public ResponseEntity<ApiErrorResponse> handleMethodArgumentTypeMismatch(
			MethodArgumentTypeMismatchException ex, WebRequest request) {
		log.warn("Argument type mismatch: {}", ex.getMessage());

		String message = "Parametro invalido: " + ex.getName();
		return buildResponse(HttpStatus.BAD_REQUEST, "Bad Request", message, request);
	}

	@ExceptionHandler(NoHandlerFoundException.class)
	public ResponseEntity<ApiErrorResponse> handleNoHandlerFound(
			NoHandlerFoundException ex, WebRequest request) {
		log.warn("No handler found: {}", ex.getRequestURL());
		return buildResponse(HttpStatus.NOT_FOUND, "Not Found", "Endpoint nao encontrado", request);
	}

	@ExceptionHandler(Exception.class)
	public ResponseEntity<ApiErrorResponse> handleGlobalException(
			Exception ex, WebRequest request) {
		log.error("Erro inesperado", ex);

		return buildResponse(
				HttpStatus.INTERNAL_SERVER_ERROR,
				"Internal Server Error",
				"Ocorreu um erro inesperado",
				request);
	}

	@ExceptionHandler(NoResourceFoundException.class)
	public ResponseEntity<ApiErrorResponse> handleNoResourceException(
			NoResourceFoundException ex, WebRequest request) {
		log.warn("No resource exception: {}", ex.getMessage());
		return buildResponse(HttpStatus.NOT_FOUND, "Not Found", ex.getMessage(), request);
	}

	private ResponseEntity<ApiErrorResponse> buildResponse(
			HttpStatus status,
			String error,
			String message,
			WebRequest request) {
		ApiErrorResponse response = new ApiErrorResponse(
				status.value(),
				error,
				message,
				extractPath(request));
		return new ResponseEntity<>(response, status);
	}

	private String extractPath(WebRequest request) {
		return request.getDescription(false).replace("uri=", "");
	}

	private String buildFieldErrorsMessage(MethodArgumentNotValidException ex) {
		List<String> errors = ex.getBindingResult().getFieldErrors().stream()
				.map(fieldError -> fieldError.getField() + ": " + fieldError.getDefaultMessage())
				.collect(Collectors.toList());

		if (errors.isEmpty()) {
			return "Erro na validacao dos dados";
		}

		return String.join("; ", errors);
	}

	private String resolveConstraintName(DataIntegrityViolationException ex) {
		Throwable cause = ex.getCause();
		while (cause != null) {
			if (cause instanceof org.hibernate.exception.ConstraintViolationException) {
				return ((org.hibernate.exception.ConstraintViolationException) cause)
						.getConstraintName();
			}
			cause = cause.getCause();
		}
		return null;
	}

	private boolean isUniqueConstraint(String constraintName) {
		String normalized = constraintName.toLowerCase(Locale.ROOT);
		return normalized.contains("unique") || normalized.contains("uk_") || normalized.contains("uniq");
	}

	private boolean containsUniqueKeyword(String message) {
		if (message == null) {
			return false;
		}
		return message.toLowerCase(Locale.ROOT).contains("unique");
	}
}
