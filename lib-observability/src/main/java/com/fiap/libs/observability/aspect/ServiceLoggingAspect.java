package com.fiap.libs.observability.aspect;

import com.fiap.libs.observability.annotation.StepLog;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.AfterThrowing;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.springframework.stereotype.Component;

/**
 * Aspect for logging service layer methods annotated with {@link StepLog}.
 * <p>
 * Provides detailed logging of method entry, success, and error scenarios.
 * </p>
 *
 * @author FIAP Unified Service Core
 * @since 1.0.0
 */
@Aspect
@Component
@Slf4j
public class ServiceLoggingAspect {

    /**
     * Logs method entry with parameters
     */
    @Before("@annotation(stepLog)")
    public void logBefore(JoinPoint joinPoint, StepLog stepLog) {
        Object[] params = joinPoint.getArgs();
        log.info("⏰ [STEP-TRY] Try: {} -> params={}", stepLog.value(), params);
    }

    /**
     * Logs successful method completion with return value
     */
    @AfterReturning(pointcut = "@annotation(stepLog)", returning = "result")
    public void logAfterReturning(JoinPoint joinPoint, StepLog stepLog, Object result) {
        log.info("✅ [STEP-SUCCESS] method: {} Success: {} -> return = {}",
                joinPoint.getSignature().getName(),
                stepLog.value(),
                result);
    }

    /**
     * Logs method exceptions with error details
     */
    @AfterThrowing(pointcut = "@annotation(stepLog)", throwing = "ex")
    public void logAfterThrowing(JoinPoint joinPoint, StepLog stepLog, Throwable ex) {
        log.error("❌ [STEP-ERROR] Error on {} in method {}: {}",
                stepLog.value(),
                joinPoint.getSignature().getName(),
                ex.getMessage());
    }
}