package com.fiap.libs.exception.exceptions;

import com.fiap.libs.exception.enums.ErrorCode;
import com.fiap.libs.exception.handler.BaseException;

public class MethodNotAllowedException extends BaseException {
    
    public MethodNotAllowedException(String message) {
        super(message, ErrorCode.METHOD_NOT_ALLOWED);
    }
}
