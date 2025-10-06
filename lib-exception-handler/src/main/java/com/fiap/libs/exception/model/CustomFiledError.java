package com.fiap.libs.exception.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CustomFiledError {
    private String field;        // Nome do campo
    private String message;      // Mensagem de erro
    private Object rejectedValue; // Valor que foi rejeitado
}
