package com.fiap.libs.observability.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Annotation for logging HTTP requests and responses.
 *
 * <p>Use on:</p>
 * <ul>
 *   <li>REST Controllers</li>
 *   <li>GraphQL Resolvers</li>
 *   <li>WebSocket Handlers</li>
 *   <li>Any HTTP entry point</li>
 * </ul>
 *
 * @author FIAP
 * @since 2.0.0
 */
@Target({ElementType.METHOD, ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
public @interface LogHttp {

    /**
     * Custom description for this endpoint.
     */
    String value() default "";

    /**
     * Whether to log request parameters.
     */
    boolean logArgs() default true;

    /**
     * Whether to log response body.
     */
    boolean logResult() default true;

    /**
     * Maximum length for request/response in logs.
     */
    int maxLength() default -1;
}