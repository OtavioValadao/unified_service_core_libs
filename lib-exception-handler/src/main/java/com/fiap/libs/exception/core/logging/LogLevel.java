package com.fiap.libs.exception.core.logging;

public enum LogLevel {

    /**
     * Apenas informação (não é erro real)
     */
    INFO,

    /**
     * Aviso - erro esperado/recuperável
     */
    WARN,

    /**
     * Erro - falha não esperada
     */
    ERROR,

    /**
     * Crítico - requer atenção imediata
     */
    CRITICAL;

    /**
     * Converte para lowercase (útil para logs estruturados)
     */
    public String toLowercase() {
        return this.name().toLowerCase();
    }
}