package com.fiap.libs.exception.api.exceptions.auth;

import com.fiap.libs.exception.api.enums.ErrorCode;
import com.fiap.libs.exception.api.exceptions.BaseException;

public class InvalidJwtTokenException extends BaseException {

    public InvalidJwtTokenException(String message, ErrorCode errorCode) {
        super(message, errorCode);
    }

    public InvalidJwtTokenException(String message, ErrorCode errorCode, Throwable cause) {
        super(message, errorCode, cause);
    }
}
