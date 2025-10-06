package com.fiap.libs.exception.exceptions;

import com.fiap.libs.exception.enums.ErrorCode;
import com.fiap.libs.exception.handler.BaseException;

public class UnsupportedMediaTypeException extends BaseException {
    
    public UnsupportedMediaTypeException(String message) {
        super(message, ErrorCode.UNSUPPORTED_MEDIA_TYPE);
    }
}
