package com.fiap.libs.exception.core.registry;

import com.fiap.libs.exception.core.logging.LogLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.http.HttpStatus;

/**
 * Metadados sobre como tratar cada tipo de exceção
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ExceptionMetadata {

    /** Status HTTP a ser retornado */
    private HttpStatus httpStatus;

    /** Nível de log a ser usado */
    private LogLevel logLevel;

    /** Se deve incluir stack trace na resposta */
    private boolean includeStackTrace;

    /**
     * Metadados padrão para exceções não mapeadas
     */
    public static ExceptionMetadata defaultMetadata() {
        return ExceptionMetadata.builder()
                .httpStatus(HttpStatus.INTERNAL_SERVER_ERROR)
                .logLevel(LogLevel.ERROR)
                .includeStackTrace(false)
                .build();
    }

    /**
     * Atalho para criar metadados comuns (sem stack trace)
     */
    public static ExceptionMetadata of(HttpStatus httpStatus, LogLevel logLevel) {
        return new ExceptionMetadata(httpStatus, logLevel, false);
    }
}