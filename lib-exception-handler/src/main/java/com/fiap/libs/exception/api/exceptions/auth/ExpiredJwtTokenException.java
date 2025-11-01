package com.fiap.libs.exception.api.exceptions.auth;

import com.fiap.libs.exception.api.enums.ErrorCode;
import com.fiap.libs.exception.api.exceptions.BaseException;

public class ExpiredJwtTokenException extends BaseException {

    public ExpiredJwtTokenException(String message, ErrorCode errorCode) {
        super(message, errorCode);
    }

    public ExpiredJwtTokenException(String message, Throwable cause) {
        super(message, ErrorCode.BAD_REQUEST, cause);
    }
}
