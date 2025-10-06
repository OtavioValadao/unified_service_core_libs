package com.fiap.libs.exception.exceptions;

import com.fiap.libs.exception.enums.ErrorCode;
import com.fiap.libs.exception.handler.BaseException;

public class InvalidFormatException extends BaseException {
    
    public InvalidFormatException(String message) {
        super(message, ErrorCode.INVALID_FORMAT);
    }
    
    public InvalidFormatException(String fieldName, String expectedFormat) {
        super(String.format("Invalid format for '%s'. Expected format: %s", fieldName, expectedFormat), 
              ErrorCode.INVALID_FORMAT);
    }
}
