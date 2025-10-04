package com.fiap.libs.observability.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Universal annotation for logging operations across all architectural layers.
 *
 * <p>Works with:</p>
 * <ul>
 *   <li>Services (traditional layers)</li>
 *   <li>Use Cases (Clean Architecture)</li>
 *   <li>Application Services (Hexagonal)</li>
 *   <li>Domain Services (DDD)</li>
 *   <li>Repositories</li>
 *   <li>Any business logic component</li>
 * </ul>
 *
 * @author FIAP
 * @since 2.0.0
 */
@Target({ElementType.METHOD, ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
public @interface LogOperation {

    /**
     * Description of the operation being performed.
     * If empty, uses method name.
     */
    String value() default "";

    /**
     * Whether to log method parameters.
     */
    boolean logArgs() default true;

    /**
     * Whether to log return value.
     */
    boolean logResult() default true;

    /**
     * Maximum length for args/result in logs.
     * -1 means use global configuration.
     */
    int maxLength() default -1;
}