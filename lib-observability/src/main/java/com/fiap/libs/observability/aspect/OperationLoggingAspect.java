package com.fiap.libs.observability.aspect;

import com.fiap.libs.observability.annotation.LogOperation;
import com.fiap.libs.observability.utils.LoggingUtils;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.stereotype.Component;

import java.lang.reflect.Method;

/**
 * Aspect for logging business operations across all architectural layers.
 *
 * <p>Supports:</p>
 * <ul>
 *   <li>Layered Architecture (Services, Repositories)</li>
 *   <li>Hexagonal Architecture (Ports, Adapters, Use Cases)</li>
 *   <li>Clean Architecture (Use Cases, Gateways, Presenters)</li>
 *   <li>Onion Architecture (Application Services, Domain Services)</li>
 * </ul>
 *
 * @author FIAP
 * @since 2.0.0
 */
@Aspect
@Component
@Slf4j
public class OperationLoggingAspect implements Ordered {

    @Value("${observability.operation.max-length:200}")
    private int defaultMaxLength;

    @Value("${observability.operation.order:#{T(org.springframework.core.Ordered).LOWEST_PRECEDENCE - 500}}")
    private int order;

    @Override
    public int getOrder() {
        return order;
    }

    /**
     * Intercepts methods annotated with @LogOperation
     */
    @Around("@within(com.fiap.libs.observability.annotation.LogOperation) || " +
            "@annotation(com.fiap.libs.observability.annotation.LogOperation)")
    public Object logOperation(ProceedingJoinPoint joinPoint) throws Throwable {

        LogOperation annotation = findAnnotation(joinPoint);
        if (annotation == null) {
            return joinPoint.proceed();
        }

        String operation = getOperationName(joinPoint, annotation);
        int maxLength = annotation.maxLength() == -1 ? defaultMaxLength : annotation.maxLength();

        // 🔵 Log INÍCIO da operação
        if (annotation.logArgs()) {
            String args = formatArguments(joinPoint.getArgs(), maxLength);
            log.info("⏰ [▶ START] {} → args: {}", operation, args);
        } else {
            log.info("⏰ [▶ START] {}", operation);
        }

        long startTime = System.currentTimeMillis();
        Object result;

        try {
            result = joinPoint.proceed();
        } catch (Throwable ex) {
            // ❌ Log ERRO
            long duration = System.currentTimeMillis() - startTime;
            log.error("❌ [✗ ERROR] {} ✗ {}ms - {}: {}",
                    operation, duration, ex.getClass().getSimpleName(), ex.getMessage());
            throw ex;
        }

        // ✅ Log SUCESSO
        long duration = System.currentTimeMillis() - startTime;
        if (annotation.logResult()) {
            String resultStr = LoggingUtils.formatArguments(result, maxLength);
            log.info("✅ [✓ SUCCESS] {} ✓ {}ms → result: {}",
                    operation, duration, resultStr);
        } else {
            log.info("✅ [✓ SUCCESS] {} ✓ {}ms", operation, duration);
        }

        return result;
    }

    private LogOperation findAnnotation(ProceedingJoinPoint joinPoint) {
        MethodSignature signature = (MethodSignature) joinPoint.getSignature();
        Method method = signature.getMethod();

        // Tenta pegar do método primeiro
        LogOperation annotation = AnnotationUtils.findAnnotation(method, LogOperation.class);
        if (annotation != null) {
            return annotation;
        }

        // Depois tenta da classe
        return AnnotationUtils.findAnnotation(joinPoint.getTarget().getClass(), LogOperation.class);
    }

    private String getOperationName(ProceedingJoinPoint joinPoint, LogOperation annotation) {
        if (!annotation.value().isEmpty()) {
            return annotation.value();
        }

        String className = joinPoint.getSignature().getDeclaringType().getSimpleName();
        String methodName = joinPoint.getSignature().getName();
        return className + "." + methodName;
    }

    private String formatArguments(Object[] args, int maxLength) {
        if (args == null || args.length == 0) {
            return "[]";
        }

        StringBuilder sb = new StringBuilder("[");
        for (int i = 0; i < args.length; i++) {
            if (i > 0) sb.append(", ");
            String argStr = LoggingUtils.sanitize(String.valueOf(args[i]));
            sb.append(LoggingUtils.truncate(argStr, maxLength));
        }
        sb.append("]");
        return sb.toString();
    }

}