package com.fiap.libs.exception.exceptions;

import com.fiap.libs.exception.enums.ErrorCode;
import com.fiap.libs.exception.handler.BaseException;

public class PayloadTooLargeException extends BaseException {
    
    public PayloadTooLargeException(String message) {
        super(message, ErrorCode.PAYLOAD_TOO_LARGE);
    }
    
    public PayloadTooLargeException(long maxSize) {
        super(String.format("Payload exceeds maximum size of %d bytes", maxSize), 
              ErrorCode.PAYLOAD_TOO_LARGE);
    }
}
