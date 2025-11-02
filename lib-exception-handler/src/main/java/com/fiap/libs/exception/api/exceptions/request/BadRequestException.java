package com.fiap.libs.exception.api.exceptions.request;


import com.fiap.libs.exception.api.enums.ErrorCode;
import com.fiap.libs.exception.api.exceptions.BaseException;

public class BadRequestException extends BaseException {
    
    public BadRequestException(String message) {
        super(message, ErrorCode.BAD_REQUEST);
    }
    
    public BadRequestException(String message, Throwable cause) {
        super(message, ErrorCode.BAD_REQUEST, cause);
    }
}
