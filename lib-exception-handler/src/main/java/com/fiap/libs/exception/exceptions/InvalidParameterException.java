package com.fiap.libs.exception.exceptions;

import com.fiap.libs.exception.enums.ErrorCode;
import com.fiap.libs.exception.handler.BaseException;

public class InvalidParameterException extends BaseException {
    
    public InvalidParameterException(String message) {
        super(message, ErrorCode.INVALID_PARAMETER);
    }
    
    public InvalidParameterException(String parameterName, String reason) {
        super(String.format("Invalid parameter '%s': %s", parameterName, reason), 
              ErrorCode.INVALID_PARAMETER);
    }
}
