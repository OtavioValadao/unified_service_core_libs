package com.fiap.libs.exception.api.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class CustomFieldError {

    private String field;
    private String message;
    private Object rejectedValue;
    private String code; // Ex: "NotNull", "Size", "Pattern"
}
