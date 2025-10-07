package com.fiap.libs.exception.core.handler;

import com.fiap.libs.exception.api.enums.ErrorCode;
import com.fiap.libs.exception.api.model.CustomFieldError;
import com.fiap.libs.exception.api.model.ErrorResponse;
import org.springframework.validation.FieldError;

import java.time.LocalDateTime;
import java.util.UUID;

/**
 * Classe base para todos os Exception Handlers
 * Contém métodos utilitários compartilhados
 */
public abstract class BaseExceptionHandler {

    /**
     * Gera um UUID único para rastreamento de erros
     *
     * @return String UUID no formato padrão
     */
    protected String generateTraceId() {
        return UUID.randomUUID().toString();
    }

    /**
     * Constrói uma resposta de erro padronizada
     *
     * @param errorCode Código do erro (enum)
     * @param message Mensagem descritiva
     * @param path URI da requisição
     * @param traceId ID de rastreamento
     * @return ErrorResponse construído
     */
    protected ErrorResponse buildErrorResponse(
            ErrorCode errorCode,
            String message,
            String path,
            String traceId) {

        return ErrorResponse.builder()
                .code(errorCode)
                .message(message)
                .path(path)
                .timestamp(LocalDateTime.now())
                .traceId(traceId)
                .build();
    }

    /**
     * Mapeia um FieldError do Spring para CustomFieldError
     *
     * @param fieldError Erro de validação do Spring
     * @return CustomFieldError formatado
     */
    protected CustomFieldError mapFieldError(FieldError fieldError) {
        return CustomFieldError.builder()
                .field(fieldError.getField())
                .message(fieldError.getDefaultMessage())
                .rejectedValue(fieldError.getRejectedValue())
                .build();
    }
}