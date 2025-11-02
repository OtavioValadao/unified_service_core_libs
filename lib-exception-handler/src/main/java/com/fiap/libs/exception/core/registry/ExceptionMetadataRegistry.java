package com.fiap.libs.exception.core.registry;

import com.fiap.libs.exception.api.exceptions.auth.ExpiredJwtTokenException;
import com.fiap.libs.exception.api.exceptions.auth.InvalidJwtTokenException;
import com.fiap.libs.exception.api.exceptions.auth.JwtTokenGenerationException;
import com.fiap.libs.exception.api.exceptions.auth.TokenRevocationException;
import com.fiap.libs.exception.api.exceptions.request.*;
import com.fiap.libs.exception.api.exceptions.resource.ResourceAlreadyExistsException;
import com.fiap.libs.exception.api.exceptions.resource.ResourceNotFoundException;
import com.fiap.libs.exception.api.exceptions.validation.InvalidFormatException;
import com.fiap.libs.exception.api.exceptions.validation.InvalidParameterException;
import com.fiap.libs.exception.api.exceptions.validation.MissingRequiredFieldException;
import com.fiap.libs.exception.api.exceptions.validation.ValidationException;
import com.fiap.libs.exception.core.logging.LogLevel;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

/**
 * Registro centralizado de metadados para cada tipo de exceção
 * Define como cada exceção deve ser tratada (status HTTP, nível de log, etc.)
 */
@Component
public class ExceptionMetadataRegistry {

    private final Map<Class<? extends Exception>, ExceptionMetadata> registry = new HashMap<>();

    public ExceptionMetadataRegistry() {
        registerAllExceptions();
    }

    /**
     * Registra metadados para todas as exceções customizadas
     */
    private void registerAllExceptions() {
        this.register(ResourceNotFoundException.class,
                HttpStatus.NOT_FOUND,
                LogLevel.WARN);

        this.register(BadRequestException.class,
                HttpStatus.BAD_REQUEST,
                LogLevel.ERROR);

        this.register(InvalidFormatException.class,
                HttpStatus.BAD_REQUEST,
                LogLevel.ERROR);

        this.register(MissingRequiredFieldException.class,
                HttpStatus.BAD_REQUEST,
                LogLevel.ERROR);

        this.register(ValidationException.class,
                HttpStatus.BAD_REQUEST,
                LogLevel.ERROR);

        this.register(ResourceAlreadyExistsException.class,
                HttpStatus.CONFLICT,
                LogLevel.WARN);

        this.register(UnsupportedMediaTypeException.class,
                HttpStatus.UNSUPPORTED_MEDIA_TYPE,
                LogLevel.ERROR);

        this.register(MethodNotAllowedException.class,
                HttpStatus.METHOD_NOT_ALLOWED,
                LogLevel.ERROR);

        this.register(InvalidParameterException.class,
                HttpStatus.UNPROCESSABLE_ENTITY,
                LogLevel.ERROR);

        this.register(PayloadTooLargeException.class,
                HttpStatus.PAYLOAD_TOO_LARGE,
                LogLevel.WARN);

        this.register(TooManyRequestsException.class,
                HttpStatus.TOO_MANY_REQUESTS,
                LogLevel.WARN);

        this.register(
                ExpiredJwtTokenException.class,
                HttpStatus.UNAUTHORIZED,
                LogLevel.WARN
        );

        this.register(
                InvalidJwtTokenException.class,
                HttpStatus.UNAUTHORIZED,
                LogLevel.ERROR
        );

        this.register(
                JwtTokenGenerationException.class,
                HttpStatus.UNAUTHORIZED,
                LogLevel.ERROR
        );

        this.register(
                TokenRevocationException.class,
                HttpStatus.UNAUTHORIZED,
                LogLevel.ERROR
        );
    }

    /**
     * Registra metadados para um tipo de exceção
     */
    private void register(
            Class<? extends Exception> exceptionClass,
            HttpStatus httpStatus,
            LogLevel logLevel) {

        registry.put(exceptionClass, ExceptionMetadata.of(httpStatus, logLevel));
    }

    /**
     * Obtém metadados para uma exceção
     * Se não estiver registrada, retorna metadados padrão
     */
    public ExceptionMetadata getMetadata(Class<? extends Exception> exClass) {
        return registry.getOrDefault(exClass, ExceptionMetadata.defaultMetadata());
    }

    /**
     * Verifica se uma exceção está registrada
     */
    public boolean isRegistered(Class<? extends Exception> exClass) {
        return registry.containsKey(exClass);
    }

    /**
     * Retorna todas as exceções registradas
     */
    public Map<Class<? extends Exception>, ExceptionMetadata> getAllRegistrations() {
        return new HashMap<>(registry);
    }
}