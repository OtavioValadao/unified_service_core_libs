package com.fiap.libs.observability.utils;

public class LoggingUtils {

    public static String sanitize(String value) {
        return value
                .replaceAll("(?i)(password|senha|pwd|token|secret|bearer|authorization)\\s*[=:]\\s*[^,\\s}]+", "$1=***");
    }

    public static String truncate(String value, int maxLength) {
        if (value.length() <= maxLength) {
            return value;
        }
        return value.substring(0, maxLength) + "...";
    }

    public static String formatArguments(Object result, int maxLength) {
        if (result == null) {
            return "null";
        }
        String resultStr = sanitize(String.valueOf(result));
        return truncate(resultStr, maxLength);
    }
}
