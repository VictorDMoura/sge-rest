package com.sgerest.exception;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public record ApiErrorResponse(
		String timestamp,
		int status,
		String error,
		String message,
		String path) {

	public ApiErrorResponse(int status, String error, String message, String path) {
		this(
				LocalDateTime.now().format(DateTimeFormatter.ISO_DATE_TIME),
				status,
				error,
				message,
				path);
	}
}
