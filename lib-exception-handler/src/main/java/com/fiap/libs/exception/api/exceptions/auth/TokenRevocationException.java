package com.fiap.libs.exception.api.exceptions.auth;

import com.fiap.libs.exception.api.enums.ErrorCode;
import com.fiap.libs.exception.api.exceptions.BaseException;

public class TokenRevocationException extends BaseException {
    protected TokenRevocationException(String message, ErrorCode errorCode) {
        super(message, errorCode);
    }

    protected TokenRevocationException(String message, ErrorCode errorCode, Throwable cause) {
        super(message, errorCode, cause);
    }
}
