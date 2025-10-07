package com.fiap.libs.exception.api.exceptions.client;

import com.fiap.libs.exception.api.enums.ErrorCode;
import com.fiap.libs.exception.api.exceptions.BaseException;

public class InvalidParameterException extends BaseException {
    
    public InvalidParameterException(String message) {
        super(message, ErrorCode.INVALID_PARAMETER);
    }
    
    public InvalidParameterException(String parameterName, String reason) {
        super(String.format("Invalid parameter '%s': %s", parameterName, reason), 
              ErrorCode.INVALID_PARAMETER);
    }
}
