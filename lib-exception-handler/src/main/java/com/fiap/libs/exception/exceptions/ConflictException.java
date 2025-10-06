package com.fiap.libs.exception.exceptions;

import com.fiap.libs.exception.enums.ErrorCode;
import com.fiap.libs.exception.handler.BaseException;

public class ConflictException extends BaseException {
    
    public ConflictException(String message) {
        super(message, ErrorCode.CONFLICT);
    }
}
