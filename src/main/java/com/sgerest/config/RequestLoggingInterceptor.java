package com.sgerest.config;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Component
@Log4j2
public class RequestLoggingInterceptor implements HandlerInterceptor {

    private static final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss");

    private boolean isSensitivePath(String uri) {
        return uri.contains("/login") ||
                uri.contains("/password") ||
                uri.contains("/token") ||
                uri.contains("/health") ||
                uri.contains("/actuator");
    }

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) {
        String uri = request.getRequestURI();

        if (!isSensitivePath(uri)) {
            String clientIp = getClientIp(request);
            String method = request.getMethod();
            String timestamp = LocalDateTime.now().format(formatter);

            log.info("REQUEST | IP: {} | Método: {} | Rota: {} | Horário: {}", clientIp, method, uri, timestamp);
        }

        return true;
    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler,
            Exception ex) {
        String uri = request.getRequestURI();

        if (!isSensitivePath(uri)) {
            String clientIp = getClientIp(request);
            String method = request.getMethod();
            int statusCode = response.getStatus();
            String timestamp = LocalDateTime.now().format(formatter);

            if (ex != null) {
                log.error("RESPONSE | IP: {} | Método: {} | Rota: {} | Status: {} | Horário: {} | Erro: {}",
                        clientIp, method, uri, statusCode, timestamp, ex.getMessage());
            } else {
                log.info("RESPONSE | IP: {} | Método: {} | Rota: {} | Status: {} | Horário: {}",
                        clientIp, method, uri, statusCode, timestamp);
            }
        }
    }

    /**
     * Extrai o IP do cliente da requisição
     * Considera proxy headers (X-Forwarded-For) e headers personalizados
     */
    private String getClientIp(HttpServletRequest request) {
        String xForwardedFor = request.getHeader("X-Forwarded-For");
        if (xForwardedFor != null && !xForwardedFor.isEmpty()) {
            return xForwardedFor.split(",")[0].trim();
        }

        String xRealIp = request.getHeader("X-Real-IP");
        if (xRealIp != null && !xRealIp.isEmpty()) {
            return xRealIp;
        }

        return request.getRemoteAddr();
    }
}
