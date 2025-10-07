package com.fiap.libs.exception.api.exceptions.client;

import com.fiap.libs.exception.api.enums.ErrorCode;
import com.fiap.libs.exception.api.exceptions.BaseException;

public class ConflictException extends BaseException {
    
    public ConflictException(String message) {
        super(message, ErrorCode.CONFLICT);
    }
}
