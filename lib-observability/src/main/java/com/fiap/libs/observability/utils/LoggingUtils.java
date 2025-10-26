package com.fiap.libs.observability.utils;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class LoggingUtils {

    private static final ObjectMapper OBJECT_MAPPER = createObjectMapper();
    private static final ObjectMapper PRETTY_OBJECT_MAPPER = createPrettyObjectMapper();
    private static boolean prettyPrintEnabled = false;

    /**
     * Configura o ObjectMapper para serialização JSON (formato compacto).
     */
    private static ObjectMapper createObjectMapper() {
        ObjectMapper mapper = new ObjectMapper();
        mapper.registerModule(new JavaTimeModule());
        mapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
        mapper.disable(SerializationFeature.FAIL_ON_EMPTY_BEANS);
        return mapper;
    }

    /**
     * Configura o ObjectMapper para serialização JSON com pretty print.
     */
    private static ObjectMapper createPrettyObjectMapper() {
        ObjectMapper mapper = new ObjectMapper();
        mapper.registerModule(new JavaTimeModule());
        mapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
        mapper.disable(SerializationFeature.FAIL_ON_EMPTY_BEANS);
        mapper.enable(SerializationFeature.INDENT_OUTPUT);  // Pretty print
        return mapper;
    }

    /**
     * Habilita ou desabilita o pretty print (JSON formatado com quebras de linha).
     * 
     * @param enabled true para habilitar pretty print, false para JSON compacto (padrão)
     */
    public static void setPrettyPrintEnabled(boolean enabled) {
        prettyPrintEnabled = enabled;
        log.info("JSON pretty print: {}", enabled ? "enabled" : "disabled");
    }

    /**
     * Sanitiza valores sensíveis (passwords, tokens, etc).
     */
    public static String sanitize(String value) {
        if (value == null) {
            return "null";
        }
        return value
                .replaceAll("(?i)(password|senha|pwd|token|secret|bearer|authorization)\"\\s*:\\s*\"[^\"]+\"", "$1\":\"***\"")
                .replaceAll("(?i)(password|senha|pwd|token|secret|bearer|authorization)\\s*[=:]\\s*[^,\\s}]+", "$1=***");
    }

    /**
     * Trunca strings longas.
     * Se maxLength <= 0, não trunca (sem limite).
     */
    public static String truncate(String value, int maxLength) {
        if (value == null) {
            return null;
        }
        // Se maxLength <= 0, não trunca (sem limite)
        if (maxLength <= 0 || value.length() <= maxLength) {
            return value;
        }
        return value.substring(0, maxLength) + "...";
    }

    /**
     * Formata argumentos para log em formato JSON.
     */
    public static String formatArguments(Object result, int maxLength) {
        if (result == null) {
            return "null";
        }

        String resultStr = toJson(result);
        resultStr = sanitize(resultStr);
        return truncate(resultStr, maxLength);
    }

    /**
     * Converte um objeto para JSON.
     */
    private static String toJson(Object obj) {
        if (obj == null) {
            return "null";
        }

        // Se já é uma String, retorna direto
        if (obj instanceof String) {
            return (String) obj;
        }

        // Se é um tipo primitivo ou wrapper, retorna toString()
        if (isPrimitiveOrWrapper(obj)) {
            return String.valueOf(obj);
        }

        try {
            // Usa o mapper apropriado (pretty ou compacto)
            ObjectMapper mapper = prettyPrintEnabled ? PRETTY_OBJECT_MAPPER : OBJECT_MAPPER;
            return mapper.writeValueAsString(obj);
        } catch (Exception e) {
            log.debug("Failed to serialize object to JSON: {} - falling back to toString()", 
                    e.getMessage());
            return String.valueOf(obj);
        }
    }

    /**
     * Verifica se o objeto é um tipo primitivo ou wrapper.
     */
    private static boolean isPrimitiveOrWrapper(Object obj) {
        return obj instanceof Number || 
               obj instanceof Boolean || 
               obj instanceof Character;
    }
}
