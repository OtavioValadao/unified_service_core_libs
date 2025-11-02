package com.fiap.libs.exception.api.exceptions.request;

import com.fiap.libs.exception.api.enums.ErrorCode;
import com.fiap.libs.exception.api.exceptions.BaseException;

public class UnsupportedMediaTypeException extends BaseException {
    
    public UnsupportedMediaTypeException(String message) {
        super(message, ErrorCode.UNSUPPORTED_MEDIA_TYPE);
    }
}
