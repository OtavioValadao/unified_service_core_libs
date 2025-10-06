package com.fiap.libs.exception.exceptions;

import com.fiap.libs.exception.enums.ErrorCode;
import com.fiap.libs.exception.handler.BaseException;

public class ResourceAlreadyExistsException extends BaseException {
    
    public ResourceAlreadyExistsException(String message) {
        super(message, ErrorCode.RESOURCE_ALREADY_EXISTS);
    }
    
    public ResourceAlreadyExistsException(String resourceName, String identifier) {
        super(String.format("%s already exists with identifier: %s", resourceName, identifier), 
              ErrorCode.RESOURCE_ALREADY_EXISTS);
    }
}
