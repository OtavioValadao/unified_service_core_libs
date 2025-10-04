package com.fiap.libs.observability.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Annotation to enable step-by-step logging on service methods.
 * <p>
 * Logs method entry, successful completion, and exceptions with contextual information.
 * </p>
 *
 * @author FIAP Unified Service Core
 * @since 1.0.0
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface StepLog {
    /**
     * Description of the step being logged
     */
    String value();
}