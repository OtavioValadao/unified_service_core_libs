package com.fiap.libs.exception.api.exceptions.auth;

import com.fiap.libs.exception.api.enums.ErrorCode;
import com.fiap.libs.exception.api.exceptions.BaseException;

public class ExpiredJwtTokenException extends BaseException {

    protected ExpiredJwtTokenException(String message, ErrorCode errorCode) {
        super(message, errorCode);
    }

    protected ExpiredJwtTokenException(String message, Throwable cause) {
        super(message, ErrorCode.BAD_REQUEST, cause);
    }
}
