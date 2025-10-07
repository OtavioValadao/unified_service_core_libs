package com.fiap.libs.exception.api.model;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fiap.libs.exception.api.enums.ErrorCode;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ErrorResponse {

    // Código do erro (enum)
    private ErrorCode code;

    // Mensagem descritiva do erro
    private String message;

    // Caminho/URI da requisição que falhou
    private String path;

    // Data/hora em que o erro ocorreu
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime timestamp;

    // ID único para rastreamento (UUID)
    private String traceId;

    // Lista de erros de validação (opcional)
    private List<CustomFieldError> errors;

    // Metadados adicionais (opcional)
    private Map<String, Object> metadata;
}
