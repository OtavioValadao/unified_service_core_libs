package com.fiap.libs.exception.exceptions;

import com.fiap.libs.exception.enums.ErrorCode;
import com.fiap.libs.exception.handler.BaseException;

public class MissingRequiredFieldException extends BaseException {
    
    public MissingRequiredFieldException(String fieldName) {
        super(String.format("Required field '%s' is missing", fieldName), 
              ErrorCode.MISSING_REQUIRED_FIELD);
    }
}
