package com.fiap.libs.exception.api.exceptions.resource;


import com.fiap.libs.exception.api.enums.ErrorCode;
import com.fiap.libs.exception.api.exceptions.BaseException;

/**
 * Exceção lançada quando um recurso não é encontrado
 * HTTP Status: 404 NOT FOUND
 */
public class ResourceNotFoundException extends BaseException {

    /**
     * Construtor com mensagem personalizada
     *
     * @param message Mensagem de erro
     */
    public ResourceNotFoundException(String message) {
        super(message, ErrorCode.RESOURCE_NOT_FOUND);
    }

    /**
     * Construtor com nome do recurso e identificador
     *
     * @param resourceName Nome do recurso (ex: "User", "Order")
     * @param id Identificador do recurso
     */
    public ResourceNotFoundException(String resourceName, Object id) {
        super(String.format("%s not found with id: %s", resourceName, id),
                ErrorCode.RESOURCE_NOT_FOUND);
    }

    /**
     * Construtor com nome do recurso, campo e valor
     *
     * @param resourceName Nome do recurso
     * @param fieldName Nome do campo
     * @param fieldValue Valor do campo
     */
    public ResourceNotFoundException(String resourceName, String fieldName, Object fieldValue) {
        super(String.format("%s not found with %s: %s", resourceName, fieldName, fieldValue),
                ErrorCode.RESOURCE_NOT_FOUND);
    }
}