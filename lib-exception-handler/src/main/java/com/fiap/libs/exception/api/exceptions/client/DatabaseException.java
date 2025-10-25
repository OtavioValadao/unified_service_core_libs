package com.fiap.libs.exception.api.exceptions.client;

import com.fiap.libs.exception.api.enums.ErrorCode;
import com.fiap.libs.exception.api.exceptions.BaseException;

/**
 * Exception lançada quando ocorre um erro ao acessar o banco de dados
 */
public class DatabaseException extends BaseException {
    
    public DatabaseException(String message) {
        super(message, ErrorCode.INTERNAL_SERVER_ERROR);
    }
    
    public DatabaseException(String message, Throwable cause) {
        super(message, ErrorCode.INTERNAL_SERVER_ERROR, cause);
    }
}

