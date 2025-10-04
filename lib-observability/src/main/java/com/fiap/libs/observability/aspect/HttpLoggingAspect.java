package com.fiap.libs.observability.aspect;

import com.fiap.libs.observability.annotation.LogHttp;
import com.fiap.libs.observability.utils.LoggingUtils;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import java.lang.reflect.Method;

/**
 * Aspect for logging HTTP requests and responses.
 *
 * <p>Works with any HTTP entry point regardless of architecture:</p>
 * <ul>
 *   <li>Controllers (Layered)</li>
 *   <li>Adapters (Hexagonal)</li>
 *   <li>Controllers/Presenters (Clean Architecture)</li>
 *   <li>API Layer (Onion)</li>
 * </ul>
 *
 * @author FIAP
 * @since 2.0.0
 */
@Aspect
@Component
@Slf4j
public class HttpLoggingAspect implements Ordered {

    @Value("${observability.http.max-length:200}")
    private int defaultMaxLength;

    @Value("${observability.http.order:#{T(org.springframework.core.Ordered).LOWEST_PRECEDENCE - 1000}}")
    private int order;

    @Override
    public int getOrder() {
        return order;
    }

    @Around("@within(com.fiap.libs.observability.annotation.LogHttp) || " +
            "@annotation(com.fiap.libs.observability.annotation.LogHttp)")
    public Object logHttpRequest(ProceedingJoinPoint joinPoint) throws Throwable {

        LogHttp annotation = findAnnotation(joinPoint);
        if (annotation == null) {
            return joinPoint.proceed();
        }

        ServletRequestAttributes attributes =
                (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();

        if (attributes == null) {
            return joinPoint.proceed(); // Não é contexto HTTP
        }

        HttpServletRequest request = attributes.getRequest();
        String httpMethod = request.getMethod();
        String endpoint = buildEndpoint(request);
        String description = getDescription(joinPoint, annotation);
        int maxLength = annotation.maxLength() == -1 ? defaultMaxLength : annotation.maxLength();

        // 🔗 Log REQUEST
        if (annotation.logArgs()) {
            String args = formatArguments(joinPoint.getArgs(), maxLength);
            log.info("🔗 [⯈ IN ] {} {} → {} {}", httpMethod, endpoint, description, args);
        } else {
            log.info("🔗 [⯈ IN ] {} {} → {}", httpMethod, endpoint, description);
        }

        long startTime = System.currentTimeMillis();
        Object result;

        try {
            result = joinPoint.proceed();
        } catch (Throwable ex) {
            // ⚠️ Log EXCEPTION
            long duration = System.currentTimeMillis() - startTime;
            log.error("⚠️ [⬅ OUT] {} {} ✗ {}ms - {}",
                    httpMethod, endpoint, duration, ex.getClass().getSimpleName());
            throw ex;
        }

        // ✅ Log RESPONSE
        long duration = System.currentTimeMillis() - startTime;
        if (annotation.logResult()) {
            String resultStr = LoggingUtils.formatArguments(result, maxLength);
            log.info("✅ [⬅ OUT] {} {} ✓ {}ms → {}",
                    httpMethod, endpoint, duration, resultStr);
        } else {
            log.info("✅ [⬅ OUT] {} {} ✓ {}ms", httpMethod, endpoint, duration);
        }

        return result;
    }

    private LogHttp findAnnotation(ProceedingJoinPoint joinPoint) {
        MethodSignature signature = (MethodSignature) joinPoint.getSignature();
        Method method = signature.getMethod();

        LogHttp annotation = AnnotationUtils.findAnnotation(method, LogHttp.class);
        if (annotation != null) {
            return annotation;
        }

        return AnnotationUtils.findAnnotation(joinPoint.getTarget().getClass(), LogHttp.class);
    }

    private String getDescription(ProceedingJoinPoint joinPoint, LogHttp annotation) {
        if (!annotation.value().isEmpty()) {
            return annotation.value();
        }
        return joinPoint.getSignature().toShortString();
    }

    private String buildEndpoint(HttpServletRequest request) {
        String path = request.getRequestURI();
        String query = request.getQueryString();
        return query != null ? path + "?" + query : path;
    }

    private String formatArguments(Object[] args, int maxLength) {
        if (args == null || args.length == 0) {
            return "[]";
        }

        StringBuilder sb = new StringBuilder("[");
        for (int i = 0; i < args.length; i++) {
            if (i > 0) sb.append(", ");

            Object arg = args[i];
            if (arg instanceof HttpServletRequest ||
                    arg instanceof jakarta.servlet.http.HttpServletResponse) {
                sb.append("[HttpObject]");
            } else {
                String argStr = LoggingUtils.sanitize(String.valueOf(arg));
                sb.append(LoggingUtils.truncate(argStr, maxLength));
            }
        }
        sb.append("]");
        return sb.toString();
    }
}