package com.fiap.libs.exception.api.exceptions.validation;

import com.fiap.libs.exception.api.enums.ErrorCode;
import com.fiap.libs.exception.api.exceptions.BaseException;

public class InvalidFormatException extends BaseException {
    
    public InvalidFormatException(String message) {
        super(message, ErrorCode.INVALID_FORMAT);
    }
    
    public InvalidFormatException(String fieldName, String expectedFormat) {
        super(String.format("Invalid format for '%s'. Expected format: %s", fieldName, expectedFormat), 
              ErrorCode.INVALID_FORMAT);
    }
}
