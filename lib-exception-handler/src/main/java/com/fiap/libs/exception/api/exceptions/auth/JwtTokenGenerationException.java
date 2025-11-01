package com.fiap.libs.exception.api.exceptions.auth;

import com.fiap.libs.exception.api.enums.ErrorCode;
import com.fiap.libs.exception.api.exceptions.BaseException;

public class JwtTokenGenerationException extends BaseException {
    public JwtTokenGenerationException(String message, ErrorCode errorCode) {
        super(message, errorCode);
    }

    public JwtTokenGenerationException(String message, ErrorCode errorCode, Throwable cause) {
        super(message, errorCode, cause);
    }
}
