package com.fiap.libs.exception.api.exceptions.client;

import com.fiap.libs.exception.api.enums.ErrorCode;
import com.fiap.libs.exception.api.exceptions.BaseException;

public class MissingRequiredFieldException extends BaseException {
    
    public MissingRequiredFieldException(String fieldName) {
        super(String.format("Required field '%s' is missing", fieldName), 
              ErrorCode.MISSING_REQUIRED_FIELD);
    }
}
