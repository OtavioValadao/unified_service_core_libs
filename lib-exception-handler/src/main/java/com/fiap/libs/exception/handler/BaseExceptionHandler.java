package com.fiap.libs.exception.handler;

import com.fiap.libs.exception.enums.ErrorCode;
import com.fiap.libs.exception.model.ErrorResponse;
import com.fiap.libs.exception.model.CustomFiledError;
import org.springframework.validation.FieldError;

import java.util.UUID;

/**
 * Classe base para todos os Exception Handlers
 * Contém métodos utilitários compartilhados
 */
public abstract class BaseExceptionHandler {

    /**
     * Constrói uma resposta de erro padronizada
     *
     * @param errorCode Código do erro
     * @param message Mensagem do erro
     * @param path Caminho da requisição
     * @param traceId ID de rastreamento
     * @return ErrorResponse configurado
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
                .traceId(traceId)
                .build();
    }

    /**
     * Mapeia um FieldError do Spring para o modelo customizado
     *
     * @param springFieldError Erro de campo do Spring
     * @return FieldError customizado
     */
    protected CustomFiledError mapFieldError(FieldError springFieldError) {
        return CustomFiledError.builder()
                .field(springFieldError.getField())
                .message(springFieldError.getDefaultMessage())
                .rejectedValue(springFieldError.getRejectedValue())
                .build();
    }

    /**
     * Gera um ID único para rastreamento de erros
     *
     * @return UUID como String
     */
    protected String generateTraceId() {
        return UUID.randomUUID().toString();
    }
}