package com.fiap.libs.exception.exceptions;

import com.fiap.libs.exception.enums.ErrorCode;
import com.fiap.libs.exception.handler.BaseException;

public class TooManyRequestsException extends BaseException {
    
    public TooManyRequestsException() {
        super("Too many requests. Please try again later", ErrorCode.TOO_MANY_REQUESTS);
    }
    
    public TooManyRequestsException(String message) {
        super(message, ErrorCode.TOO_MANY_REQUESTS);
    }
}
