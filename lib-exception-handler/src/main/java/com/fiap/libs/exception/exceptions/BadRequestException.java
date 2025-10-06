package com.fiap.libs.exception.exceptions;

import com.fiap.libs.exception.enums.ErrorCode;
import com.fiap.libs.exception.handler.BaseException;

public class BadRequestException extends BaseException {
    
    public BadRequestException(String message) {
        super(message, ErrorCode.BAD_REQUEST);
    }
    
    public BadRequestException(String message, Throwable cause) {
        super(message, ErrorCode.BAD_REQUEST, cause);
    }
}
