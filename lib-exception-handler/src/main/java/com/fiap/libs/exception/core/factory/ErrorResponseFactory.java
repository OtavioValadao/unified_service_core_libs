package com.fiap.libs.exception.core.factory;

import com.fiap.libs.exception.api.enums.ErrorCode;
import com.fiap.libs.exception.api.exceptions.BaseException;
import com.fiap.libs.exception.api.model.ErrorResponse;
import jakarta.servlet.http.HttpServletRequest;

import java.time.LocalDateTime;
import java.util.UUID;

/**
 * Factory para criação de ErrorResponse padronizados
 */
public final class ErrorResponseFactory {

    private ErrorResponseFactory() {
        throw new UnsupportedOperationException("Utility class");
    }

    /**
     * Cria ErrorResponse a partir de BaseException
     */
    public static ErrorResponse create(BaseException ex, HttpServletRequest request) {
        return create(ex, request, generateTraceId());
    }

    /**
     * Cria ErrorResponse com traceId customizado
     */
    public static ErrorResponse create(
            BaseException ex,
            HttpServletRequest request,
            String traceId) {

        return ErrorResponse.builder()
                .code(ex.getErrorCode())
                .message(ex.getMessage())
                .path(request.getRequestURI())
                .timestamp(LocalDateTime.now())
                .traceId(traceId)
                .build();
    }

    /**
     * Cria ErrorResponse com ErrorCode manual (para exceções do Spring)
     */
    public static ErrorResponse create(
            ErrorCode errorCode,
            String message,
            HttpServletRequest request,
            String traceId) {

        return ErrorResponse.builder()
                .code(errorCode)
                .message(message)
                .path(request.getRequestURI())
                .timestamp(LocalDateTime.now())
                .traceId(traceId)
                .build();
    }

    private static String generateTraceId() {
        return UUID.randomUUID().toString();
    }
}