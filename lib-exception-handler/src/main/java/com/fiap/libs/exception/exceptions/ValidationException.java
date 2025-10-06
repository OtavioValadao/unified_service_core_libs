package com.fiap.libs.exception.exceptions;

import com.fiap.libs.exception.enums.ErrorCode;
import com.fiap.libs.exception.handler.BaseException;

public class ValidationException extends BaseException {
    
    public ValidationException(String message) {
        super(message, ErrorCode.VALIDATION_ERROR);
    }
}
