package com.fiap.libs.exception.api.exceptions.validation;

import com.fiap.libs.exception.api.enums.ErrorCode;
import com.fiap.libs.exception.api.exceptions.BaseException;

public class ValidationException extends BaseException {
    
    public ValidationException(String message) {
        super(message, ErrorCode.VALIDATION_ERROR);
    }
}
