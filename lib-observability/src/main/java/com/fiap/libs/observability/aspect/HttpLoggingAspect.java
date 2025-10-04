package com.fiap.libs.observability.aspect;

import com.fiap.libs.observability.annotation.LogHttp;
import com.fiap.libs.observability.utils.LoggingUtils;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
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
 * <p>Features:</p>
 * <ul>
 *   <li>Automatic HTTP method and endpoint logging</li>
 *   <li>Request parameters and response body tracking</li>
 *   <li>Execution time measurement</li>
 *   <li>Exception handling with duration tracking</li>
 *   <li>Automatic sensitive data sanitization</li>
 * </ul>
 *
 * @author FIAP
 * @version 2.0.0
 * @since 2.0.0
 */
@Aspect
@Component
@Slf4j
public class HttpLoggingAspect implements Ordered {

    private static final String HTTP_INCOMING_LOG = "🔗 [⯈ IN ] {} {} → {}";
    private static final String HTTP_INCOMING_WITH_ARGS_LOG = "🔗 [⯈ IN ] {} {} → {} {}";
    private static final String HTTP_SUCCESS_LOG = "✅ [⬅ OUT] {} {} ✓ {}ms";
    private static final String HTTP_SUCCESS_WITH_RESULT_LOG = "✅ [⬅ OUT] {} {} ✓ {}ms → {}";
    private static final String HTTP_ERROR_LOG = "⚠️ [⬅ OUT] {} {} ✗ {}ms - {}";

    @Value("${observability.http.max-length:200}")
    private int defaultMaxLength;

    @Value("${observability.http.order:#{T(org.springframework.core.Ordered).LOWEST_PRECEDENCE - 1000}}")
    private int order;

    @Override
    public int getOrder() {
        return order;
    }

    /**
     * Intercepts methods annotated with @LogHttp to log HTTP requests and responses.
     *
     * @param joinPoint the proceeding join point
     * @return the result of the method execution
     * @throws Throwable if the underlying method throws an exception
     */
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
            log.debug("@LogHttp used outside HTTP context in: {}", joinPoint.getSignature());
            return joinPoint.proceed();
        }

        HttpServletRequest request = attributes.getRequest();
        String httpMethod = request.getMethod();
        String endpoint = buildEndpoint(request);
        String description = getDescription(joinPoint, annotation);
        int maxLength = annotation.maxLength() == -1 ? defaultMaxLength : annotation.maxLength();

        // 🔗 Log REQUEST
        logIncomingRequest(annotation, joinPoint, httpMethod, endpoint, description, maxLength);

        long startTime = System.currentTimeMillis();
        Object result;

        try {
            result = joinPoint.proceed();
        } catch (Throwable ex) {
            // ⚠️ Log EXCEPTION
            logException(httpMethod, endpoint, startTime, ex);
            throw ex;
        }

        // ✅ Log RESPONSE
        logSuccessResponse(annotation, httpMethod, endpoint, startTime, result, maxLength);

        return result;
    }

    /**
     * Logs the incoming HTTP request.
     */
    private void logIncomingRequest(LogHttp annotation, ProceedingJoinPoint joinPoint,
                                    String httpMethod, String endpoint, String description, int maxLength) {
        if (annotation.logArgs()) {
            String args = formatArguments(joinPoint.getArgs(), maxLength);
            log.info(HTTP_INCOMING_WITH_ARGS_LOG, httpMethod, endpoint, description, args);
        } else {
            log.info(HTTP_INCOMING_LOG, httpMethod, endpoint, description);
        }
    }

    /**
     * Logs a successful HTTP response.
     */
    private void logSuccessResponse(LogHttp annotation, String httpMethod, String endpoint,
                                    long startTime, Object result, int maxLength) {
        long duration = System.currentTimeMillis() - startTime;

        if (annotation.logResult()) {
            String resultStr = LoggingUtils.formatArguments(result, maxLength);
            log.info(HTTP_SUCCESS_WITH_RESULT_LOG, httpMethod, endpoint, duration, resultStr);
        } else {
            log.info(HTTP_SUCCESS_LOG, httpMethod, endpoint, duration);
        }
    }

    /**
     * Logs an HTTP request that resulted in an exception.
     */
    private void logException(String httpMethod, String endpoint, long startTime, Throwable ex) {
        long duration = System.currentTimeMillis() - startTime;
        log.error(HTTP_ERROR_LOG, httpMethod, endpoint, duration, ex.getClass().getSimpleName());
    }

    /**
     * Finds the @LogHttp annotation on the method or class level.
     */
    private LogHttp findAnnotation(ProceedingJoinPoint joinPoint) {
        MethodSignature signature = (MethodSignature) joinPoint.getSignature();
        Method method = signature.getMethod();

        LogHttp annotation = AnnotationUtils.findAnnotation(method, LogHttp.class);
        if (annotation != null) {
            return annotation;
        }

        return AnnotationUtils.findAnnotation(joinPoint.getTarget().getClass(), LogHttp.class);
    }

    /**
     * Gets the description for the log entry.
     * Uses annotation value if provided, otherwise falls back to method signature.
     */
    private String getDescription(ProceedingJoinPoint joinPoint, LogHttp annotation) {
        if (!annotation.value().isEmpty()) {
            return annotation.value();
        }
        return joinPoint.getSignature().toShortString();
    }

    /**
     * Builds the complete endpoint including query parameters.
     */
    private String buildEndpoint(HttpServletRequest request) {
        String path = request.getRequestURI();
        String query = request.getQueryString();
        return query != null ? path + "?" + query : path;
    }

    /**
     * Formats method arguments for logging, excluding HttpServletRequest and HttpServletResponse objects.
     */
    private String formatArguments(Object[] args, int maxLength) {
        if (args == null || args.length == 0) {
            return "[]";
        }

        StringBuilder sb = new StringBuilder(args.length * 50);
        sb.append("[");

        for (int i = 0; i < args.length; i++) {
            if (i > 0) {
                sb.append(", ");
            }

            Object arg = args[i];
            if (arg instanceof HttpServletRequest || arg instanceof HttpServletResponse) {
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