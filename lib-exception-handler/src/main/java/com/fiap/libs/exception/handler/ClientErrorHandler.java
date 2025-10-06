package com.fiap.libs.exception.handler;

import com.fiap.libs.exception.enums.ErrorCode;
import com.fiap.libs.exception.exceptions.*;
import com.fiap.libs.exception.model.ErrorResponse;
import com.fiap.libs.exception.model.CustomFiledError;
import jakarta.servlet.http.HttpServletRequest;
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

@Slf4j
@RestControllerAdvice
@Order(Ordered.HIGHEST_PRECEDENCE)
public class ClientErrorHandler extends BaseExceptionHandler {

    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleResourceNotFound(
            ResourceNotFoundException ex,
            HttpServletRequest request) {

        String traceId = generateTraceId();
        log.error("[{}] Resource not found: {}", traceId, ex.getMessage());

        ErrorResponse error = buildErrorResponse(
                ex.getErrorCode(),
                ex.getMessage(),
                request.getRequestURI(),
                traceId
        );

        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(error);
    }

    @ExceptionHandler(ResourceAlreadyExistsException.class)
    public ResponseEntity<ErrorResponse> handleResourceAlreadyExists(
            ResourceAlreadyExistsException ex,
            HttpServletRequest request) {

        String traceId = generateTraceId();
        log.error("[{}] Resource already exists: {}", traceId, ex.getMessage());

        ErrorResponse error = buildErrorResponse(
                ex.getErrorCode(),
                ex.getMessage(),
                request.getRequestURI(),
                traceId
        );

        return ResponseEntity.status(HttpStatus.CONFLICT).body(error);
    }

    @ExceptionHandler(BadRequestException.class)
    public ResponseEntity<ErrorResponse> handleBadRequest(
            BadRequestException ex,
            HttpServletRequest request) {

        String traceId = generateTraceId();
        log.error("[{}] Bad request: {}", traceId, ex.getMessage());

        ErrorResponse error = buildErrorResponse(
                ex.getErrorCode(),
                ex.getMessage(),
                request.getRequestURI(),
                traceId
        );

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
    }

    @ExceptionHandler(InvalidParameterException.class)
    public ResponseEntity<ErrorResponse> handleInvalidParameter(
            InvalidParameterException ex,
            HttpServletRequest request) {

        String traceId = generateTraceId();
        log.error("[{}] Invalid parameter: {}", traceId, ex.getMessage());

        ErrorResponse error = buildErrorResponse(
                ex.getErrorCode(),
                ex.getMessage(),
                request.getRequestURI(),
                traceId
        );

        return ResponseEntity.status(HttpStatus.UNPROCESSABLE_ENTITY).body(error);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponse> handleValidationError(
            MethodArgumentNotValidException ex,
            HttpServletRequest request) {

        String traceId = generateTraceId();

        List<CustomFiledError> fieldErrors = ex.getBindingResult()
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

    @ExceptionHandler(ValidationException.class)
    public ResponseEntity<ErrorResponse> handleValidation(
            ValidationException ex,
            HttpServletRequest request) {

        String traceId = generateTraceId();
        log.error("[{}] Validation exception: {}", traceId, ex.getMessage());

        ErrorResponse error = buildErrorResponse(
                ex.getErrorCode(),
                ex.getMessage(),
                request.getRequestURI(),
                traceId
        );

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
    }

    @ExceptionHandler(MissingRequiredFieldException.class)
    public ResponseEntity<ErrorResponse> handleMissingRequiredField(
            MissingRequiredFieldException ex,
            HttpServletRequest request) {

        String traceId = generateTraceId();
        log.error("[{}] Missing required field: {}", traceId, ex.getMessage());

        ErrorResponse error = buildErrorResponse(
                ex.getErrorCode(),
                ex.getMessage(),
                request.getRequestURI(),
                traceId
        );

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
    }

    @ExceptionHandler(InvalidFormatException.class)
    public ResponseEntity<ErrorResponse> handleInvalidFormat(
            InvalidFormatException ex,
            HttpServletRequest request) {

        String traceId = generateTraceId();
        log.error("[{}] Invalid format: {}", traceId, ex.getMessage());

        ErrorResponse error = buildErrorResponse(
                ex.getErrorCode(),
                ex.getMessage(),
                request.getRequestURI(),
                traceId
        );

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
    }

    @ExceptionHandler(ConflictException.class)
    public ResponseEntity<ErrorResponse> handleConflict(
            ConflictException ex,
            HttpServletRequest request) {

        String traceId = generateTraceId();
        log.warn("[{}] Conflict: {}", traceId, ex.getMessage());

        ErrorResponse error = buildErrorResponse(
                ex.getErrorCode(),
                ex.getMessage(),
                request.getRequestURI(),
                traceId
        );

        return ResponseEntity.status(HttpStatus.CONFLICT).body(error);
    }

    @ExceptionHandler(TooManyRequestsException.class)
    public ResponseEntity<ErrorResponse> handleTooManyRequests(
            TooManyRequestsException ex,
            HttpServletRequest request) {

        String traceId = generateTraceId();
        log.warn("[{}] Too many requests: {}", traceId, ex.getMessage());

        ErrorResponse error = buildErrorResponse(
                ex.getErrorCode(),
                ex.getMessage(),
                request.getRequestURI(),
                traceId
        );

        return ResponseEntity.status(HttpStatus.TOO_MANY_REQUESTS).body(error);
    }

    @ExceptionHandler(PayloadTooLargeException.class)
    public ResponseEntity<ErrorResponse> handlePayloadTooLarge(
            PayloadTooLargeException ex,
            HttpServletRequest request) {

        String traceId = generateTraceId();
        log.warn("[{}] Payload too large: {}", traceId, ex.getMessage());

        ErrorResponse error = buildErrorResponse(
                ex.getErrorCode(),
                ex.getMessage(),
                request.getRequestURI(),
                traceId
        );

        return ResponseEntity.status(HttpStatus.PAYLOAD_TOO_LARGE).body(error);
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<ErrorResponse> handleHttpMessageNotReadable(
            HttpMessageNotReadableException ex,
            HttpServletRequest request) {

        String traceId = generateTraceId();
        log.error("[{}] Malformed JSON request: {}", traceId, ex.getMessage());

        ErrorResponse error = buildErrorResponse(
                ErrorCode.BAD_REQUEST,
                "Malformed JSON request",
                request.getRequestURI(),
                traceId
        );

        return ResponseEntity.badRequest().body(error);
    }

    @ExceptionHandler(HttpMediaTypeNotSupportedException.class)
    public ResponseEntity<ErrorResponse> handleHttpMediaTypeNotSupported(
            HttpMediaTypeNotSupportedException ex,
            HttpServletRequest request) {

        String traceId = generateTraceId();
        log.error("[{}] Unsupported media type: {}", traceId, ex.getMessage());

        ErrorResponse error = buildErrorResponse(
                ErrorCode.UNSUPPORTED_MEDIA_TYPE,
                "Unsupported media type: " + ex.getContentType(),
                request.getRequestURI(),
                traceId
        );

        return ResponseEntity.status(HttpStatus.UNSUPPORTED_MEDIA_TYPE).body(error);
    }

    @ExceptionHandler(HttpRequestMethodNotSupportedException.class)
    public ResponseEntity<ErrorResponse> handleHttpRequestMethodNotSupported(
            HttpRequestMethodNotSupportedException ex,
            HttpServletRequest request) {

        String traceId = generateTraceId();
        log.error("[{}] Method not allowed: {}", traceId, ex.getMessage());

        ErrorResponse error = buildErrorResponse(
                ErrorCode.METHOD_NOT_ALLOWED,
                "Method " + ex.getMethod() + " not allowed",
                request.getRequestURI(),
                traceId
        );

        return ResponseEntity.status(HttpStatus.METHOD_NOT_ALLOWED).body(error);
    }

    @ExceptionHandler(MissingServletRequestParameterException.class)
    public ResponseEntity<ErrorResponse> handleMissingServletRequestParameter(
            MissingServletRequestParameterException ex,
            HttpServletRequest request) {

        String traceId = generateTraceId();
        log.error("[{}] Missing request parameter: {}", traceId, ex.getMessage());

        ErrorResponse error = buildErrorResponse(
                ErrorCode.BAD_REQUEST,
                "Missing required parameter: " + ex.getParameterName(),
                request.getRequestURI(),
                traceId
        );

        return ResponseEntity.badRequest().body(error);
    }

    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public ResponseEntity<ErrorResponse> handleMethodArgumentTypeMismatch(
            MethodArgumentTypeMismatchException ex,
            HttpServletRequest request) {

        String traceId = generateTraceId();
        log.error("[{}] Type mismatch: {}", traceId, ex.getMessage());

        String errorMessage = String.format("Invalid value for parameter '%s': expected type %s",
                ex.getName(),
                ex.getRequiredType() != null ? ex.getRequiredType().getSimpleName() : "unknown");

        ErrorResponse error = buildErrorResponse(
                ErrorCode.INVALID_PARAMETER,
                errorMessage,
                request.getRequestURI(),
                traceId
        );

        return ResponseEntity.badRequest().body(error);
    }

    @ExceptionHandler(NoHandlerFoundException.class)
    public ResponseEntity<ErrorResponse> handleNoHandlerFound(
            NoHandlerFoundException ex,
            HttpServletRequest request) {

        String traceId = generateTraceId();
        log.error("[{}] No handler found: {}", traceId, ex.getMessage());

        ErrorResponse error = buildErrorResponse(
                ErrorCode.RESOURCE_NOT_FOUND,
                "Endpoint not found",
                request.getRequestURI(),
                traceId
        );

        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(error);
    }

    @ExceptionHandler(UnsupportedMediaTypeException.class)
    public ResponseEntity<ErrorResponse> handleUnsupportedMediaType(
            UnsupportedMediaTypeException ex,
            HttpServletRequest request) {

        String traceId = generateTraceId();
        log.error("[{}] Unsupported media type: {}", traceId, ex.getMessage());

        ErrorResponse error = buildErrorResponse(
                ex.getErrorCode(),
                ex.getMessage(),
                request.getRequestURI(),
                traceId
        );

        return ResponseEntity.status(HttpStatus.UNSUPPORTED_MEDIA_TYPE).body(error);
    }

    @ExceptionHandler(MethodNotAllowedException.class)
    public ResponseEntity<ErrorResponse> handleMethodNotAllowed(
            MethodNotAllowedException ex,
            HttpServletRequest request) {

        String traceId = generateTraceId();
        log.error("[{}] Method not allowed: {}", traceId, ex.getMessage());

        ErrorResponse error = buildErrorResponse(
                ex.getErrorCode(),
                ex.getMessage(),
                request.getRequestURI(),
                traceId
        );

        return ResponseEntity.status(HttpStatus.METHOD_NOT_ALLOWED).body(error);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleGenericException(
            Exception ex,
            HttpServletRequest request) {

        String traceId = generateTraceId();
        log.error("[{}] Internal server error", traceId, ex);

        ErrorResponse error = buildErrorResponse(
                ErrorCode.INTERNAL_SERVER_ERROR,
                "An unexpected error occurred",
                request.getRequestURI(),
                traceId
        );

        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(error);
    }
}