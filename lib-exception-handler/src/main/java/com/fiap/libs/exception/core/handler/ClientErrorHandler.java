package com.fiap.libs.exception.core.handler;

import com.fiap.libs.exception.api.enums.ErrorCode;
import com.fiap.libs.exception.api.exceptions.BaseException;
import com.fiap.libs.exception.api.model.CustomFieldError;
import com.fiap.libs.exception.api.model.ErrorResponse;
import com.fiap.libs.exception.core.factory.ErrorResponseFactory;
import com.fiap.libs.exception.core.logging.LogLevel;
import com.fiap.libs.exception.core.registry.ExceptionMetadata;
import com.fiap.libs.exception.core.registry.ExceptionMetadataRegistry;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.HttpMediaTypeNotSupportedException;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.springframework.web.servlet.NoHandlerFoundException;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Handler global de exceções para APIs REST
 * Usa Strategy pattern com ExceptionMetadataRegistry para evitar duplicação
 */
@Slf4j
@RestControllerAdvice
@Order(Ordered.HIGHEST_PRECEDENCE)
@RequiredArgsConstructor
public class ClientErrorHandler extends BaseExceptionHandler {

    private final ExceptionMetadataRegistry exceptionRegistry;

    /**
     * Handler genérico para todas as exceções customizadas (BaseException)
     * Usa metadados do Registry para determinar status HTTP e nível de log
     */
    @ExceptionHandler(BaseException.class)
    public ResponseEntity<ErrorResponse> handleBaseException(
            BaseException ex,
            HttpServletRequest request) {

        String traceId = generateTraceId();
        ExceptionMetadata metadata = exceptionRegistry.getMetadata(ex.getClass());

        // Log baseado no nível configurado
        logException(traceId, ex, metadata.getLogLevel());

        // Cria resposta usando factory
        ErrorResponse error = ErrorResponseFactory.create(ex, request, traceId);

        return ResponseEntity
                .status(metadata.getHttpStatus())
                .body(error);
    }

    /**
     * Handler especial para validação de campos (@Valid)
     * Retorna lista detalhada de erros de validação
     */
    /**
     * Handler especial para validação de campos (@Valid)
     * Retorna lista detalhada de erros de validação
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponse> handleValidationError(
            MethodArgumentNotValidException ex,
            HttpServletRequest request) {

        String traceId = generateTraceId();

        // ✅ CORRIGIDO: CustomFieldError ao invés de BaseException
        List<CustomFieldError> fieldErrors = ex.getBindingResult()
                .getFieldErrors()
                .stream()
                .map(this::mapFieldError)
                .collect(Collectors.toList());

        String errorMessage = fieldErrors.stream()
                .map(err -> err.getField() + ": " + err.getMessage())
                .collect(Collectors.joining(", "));

        log.error("[{}] Validation error: {}", traceId, errorMessage);

        ErrorResponse error = ErrorResponse.builder()
                .code(ErrorCode.VALIDATION_ERROR)
                .message("Validation failed")
                .path(request.getRequestURI())
                .traceId(traceId)
                .errors(fieldErrors)
                .build();

        return ResponseEntity.badRequest().body(error);
    }

    /**
     * Handler para JSON malformado
     */
    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<ErrorResponse> handleHttpMessageNotReadable(
            HttpMessageNotReadableException ex,
            HttpServletRequest request) {

        String traceId = generateTraceId();
        log.error("[{}] Malformed JSON request: {}", traceId, ex.getMessage());

        ErrorResponse error = ErrorResponseFactory.create(
                ErrorCode.BAD_REQUEST,
                "Malformed JSON request",
                request,
                traceId
        );

        return ResponseEntity.badRequest().body(error);
    }

    /**
     * Handler para Content-Type não suportado
     */
    @ExceptionHandler(HttpMediaTypeNotSupportedException.class)
    public ResponseEntity<ErrorResponse> handleHttpMediaTypeNotSupported(
            HttpMediaTypeNotSupportedException ex,
            HttpServletRequest request) {

        String traceId = generateTraceId();
        log.error("[{}] Unsupported media type: {}", traceId, ex.getContentType());

        ErrorResponse error = ErrorResponseFactory.create(
                ErrorCode.UNSUPPORTED_MEDIA_TYPE,
                "Unsupported media type: " + ex.getContentType(),
                request,
                traceId
        );

        return ResponseEntity.status(HttpStatus.UNSUPPORTED_MEDIA_TYPE).body(error);
    }

    /**
     * Handler para método HTTP não permitido
     */
    @ExceptionHandler(HttpRequestMethodNotSupportedException.class)
    public ResponseEntity<ErrorResponse> handleHttpRequestMethodNotSupported(
            HttpRequestMethodNotSupportedException ex,
            HttpServletRequest request) {

        String traceId = generateTraceId();
        log.error("[{}] Method not allowed: {}", traceId, ex.getMethod());

        ErrorResponse error = ErrorResponseFactory.create(
                ErrorCode.METHOD_NOT_ALLOWED,
                "Method " + ex.getMethod() + " not allowed",
                request,
                traceId
        );

        return ResponseEntity.status(HttpStatus.METHOD_NOT_ALLOWED).body(error);
    }

    /**
     * Handler para parâmetro obrigatório ausente
     */
    @ExceptionHandler(MissingServletRequestParameterException.class)
    public ResponseEntity<ErrorResponse> handleMissingServletRequestParameter(
            MissingServletRequestParameterException ex,
            HttpServletRequest request) {

        String traceId = generateTraceId();
        log.error("[{}] Missing request parameter: {}", traceId, ex.getParameterName());

        ErrorResponse error = ErrorResponseFactory.create(
                ErrorCode.BAD_REQUEST,
                "Missing required parameter: " + ex.getParameterName(),
                request,
                traceId
        );

        return ResponseEntity.badRequest().body(error);
    }

    /**
     * Handler para tipo de parâmetro incompatível
     */
    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public ResponseEntity<ErrorResponse> handleMethodArgumentTypeMismatch(
            MethodArgumentTypeMismatchException ex,
            HttpServletRequest request) {

        String traceId = generateTraceId();
        log.error("[{}] Type mismatch for parameter: {}", traceId, ex.getName());

        String errorMessage = String.format(
                "Invalid value for parameter '%s': expected type %s",
                ex.getName(),
                ex.getRequiredType() != null ? ex.getRequiredType().getSimpleName() : "unknown"
        );

        ErrorResponse error = ErrorResponseFactory.create(
                ErrorCode.INVALID_PARAMETER,
                errorMessage,
                request,
                traceId
        );

        return ResponseEntity.badRequest().body(error);
    }

    /**
     * Handler para endpoint não encontrado
     */
    @ExceptionHandler(NoHandlerFoundException.class)
    public ResponseEntity<ErrorResponse> handleNoHandlerFound(
            NoHandlerFoundException ex,
            HttpServletRequest request) {

        String traceId = generateTraceId();
        log.error("[{}] No handler found for: {} {}", traceId, ex.getHttpMethod(), ex.getRequestURL());

        ErrorResponse error = ErrorResponseFactory.create(
                ErrorCode.RESOURCE_NOT_FOUND,
                "Endpoint not found",
                request,
                traceId
        );

        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(error);
    }

    /**
     * Handler fallback para exceções não mapeadas
     * Retorna 500 Internal Server Error
     */
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleGenericException(
            Exception ex,
            HttpServletRequest request) {

        String traceId = generateTraceId();
        log.error("[{}] Internal server error: {}", traceId, ex.getMessage(), ex);

        ErrorResponse error = ErrorResponseFactory.create(
                ErrorCode.INTERNAL_SERVER_ERROR,
                "An unexpected error occurred",
                request,
                traceId
        );

        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(error);
    }

    /**
     * Log estruturado baseado no nível configurado
     */
    private void logException(String traceId, Exception ex, LogLevel logLevel) {
        String message = String.format("[%s] %s: %s",
                traceId,
                ex.getClass().getSimpleName(),
                ex.getMessage());

        switch (logLevel) {
            case INFO -> log.info(message);
            case WARN -> log.warn(message);
            case ERROR -> log.error(message);
            case CRITICAL -> log.error(message, ex);
        }
    }
}