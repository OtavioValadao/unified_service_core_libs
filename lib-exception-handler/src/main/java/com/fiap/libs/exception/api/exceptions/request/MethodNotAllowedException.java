package com.fiap.libs.exception.api.exceptions.request;

import com.fiap.libs.exception.api.enums.ErrorCode;
import com.fiap.libs.exception.api.exceptions.BaseException;

public class MethodNotAllowedException extends BaseException {
    
    public MethodNotAllowedException(String message) {
        super(message, ErrorCode.METHOD_NOT_ALLOWED);
    }
}
