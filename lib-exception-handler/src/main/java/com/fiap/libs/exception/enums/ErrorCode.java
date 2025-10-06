package com.fiap.libs.exception.enums;

import lombok.Getter;

@Getter
public enum ErrorCode {

    // Client Errors (4xx)
    BAD_REQUEST("ERR_001", "Bad Request"),
    RESOURCE_NOT_FOUND("ERR_002", "Resource Not Found"),
    RESOURCE_ALREADY_EXISTS("ERR_003", "Resource Already Exists"),
    INVALID_PARAMETER("ERR_004", "Invalid Parameter"),
    VALIDATION_ERROR("ERR_010", "Validation Error"),
    MISSING_REQUIRED_FIELD("ERR_011", "Missing Required Field"),
    INVALID_FORMAT("ERR_012", "Invalid Format"),
    CONFLICT("ERR_014", "Conflict"),
    TOO_MANY_REQUESTS("ERR_015", "Too Many Requests"),
    PAYLOAD_TOO_LARGE("ERR_016", "Payload Too Large"),
    UNSUPPORTED_MEDIA_TYPE("ERR_017", "Unsupported Media Type"),
    METHOD_NOT_ALLOWED("ERR_018", "Method Not Allowed"),
    INTERNAL_SERVER_ERROR("ERR_050", "Internal Server Error");


    private final String code;
    private final String description;

    ErrorCode(String code, String description) {
        this.code = code;
        this.description = description;
    }

}
