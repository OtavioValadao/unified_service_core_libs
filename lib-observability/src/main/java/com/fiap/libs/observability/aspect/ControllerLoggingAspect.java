package com.fiap.libs.observability.aspect;

import jakarta.annotation.PostConstruct;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.regex.Pattern;

/**
 * Aspect for logging HTTP controller requests and responses.
 *
 * <p>
 * This aspect automatically intercepts all method calls within configured
 * controller packages and logs:
 * - HTTP method
 * - Endpoint URI
 * - Controller method name
 * - Method arguments
 * - Execution time
 * - Exceptions if thrown
 * </p>
 *
 * <p>
 * Configurable via property:
 * <pre>
 * observability.controller.base-packages
 * </pre>
 * Allows multiple comma-separated package patterns (e.g., "entrypoint.controller, entity.controller").
 * Default pattern "**.controller" captures any package ending with "controller".
 * </p>
 *
 * <p>
 * Designed as a reusable library component, no annotation is required on controllers.
 * </p>
 *
 * @author FIAP
 * @since 1.0.0
 */
@Aspect
@Component
@Slf4j
public class ControllerLoggingAspect {

    /**
     * Base packages to scan for controller logging.
     * Supports wildcards:
     * - ** matches zero or more packages
     * - * matches any characters except '.'
     * Can be configured in application.yml or application.properties.
     */
    @Value("${observability.controller.base-packages:**.controller}")
    private String basePackages;

    /**
     * Compiled regex patterns from basePackages.
     * Used internally to check if a class should be logged.
     */
    private List<Pattern> packagePatterns;

    /**
     * Initializes the compiled package patterns after dependency injection.
     * Converts wildcard-style package patterns to regex.
     */
    @PostConstruct
    public void init() {
        log.info("Base Packages: {}", basePackages);
        packagePatterns = Arrays.stream(basePackages.split(","))
                .map(String::trim)
                .map(this::convertToRegex)
                .map(Pattern::compile)
                .toList();
        log.info("Package Patterns: {}", packagePatterns);
    }

    /**
     * Converts wildcard package strings into proper regex patterns.
     * Example:
     * - "com.fiap.**.controller" -> "^com\.fiap\..*\.controller.*$"
     *
     * @param pkg the wildcard package string
     * @return regex string
     */
    private String convertToRegex(String pkg) {
        String regex = pkg.replace(".", "\\.").replace("**", ".*").replace("*", "[^\\.]*");
        return "^" + regex + ".*$";
    }

    /**
     * Around advice to log all controller method invocations.
     * Matches all methods, but filters classes based on configured package patterns.
     *
     * @param joinPoint the join point (method execution)
     * @return the result of the method execution
     * @throws Throwable if the target method throws
     */
    @Around("execution(* *(..))")
    public Object logController(ProceedingJoinPoint joinPoint) throws Throwable {
        // Fully qualified class name
        String className = joinPoint.getSignature().getDeclaringTypeName();

        // Skip logging if class is not in configured package patterns
        boolean matches = packagePatterns.stream().anyMatch(p -> p.matcher(className).matches());
        if (!matches) {
            return joinPoint.proceed();
        }

        // Retrieve HTTP request details
        HttpServletRequest request = ((ServletRequestAttributes) Objects.requireNonNull(
                RequestContextHolder.getRequestAttributes()
        )).getRequest();

        String methodName = request.getMethod();
        String fullPath = request.getRequestURI();
        String query = request.getQueryString();
        if (query != null) {
            fullPath += "?" + query;
        }

        // Controller method and arguments
        String controllerMethod = joinPoint.getSignature().getName();
        Object[] args = joinPoint.getArgs();

        // Log incoming request
        log.info("🔗 [ ⯈ Incoming ] HTTP method: {} - endpoint: {} - Controller [{}] args: {}",
                methodName, fullPath, controllerMethod, args);

        long startTime = System.currentTimeMillis();
        Object result;

        try {
            // Execute actual controller method
            result = joinPoint.proceed();
        } catch (Throwable ex) {
            // Log exception if occurs
            long executionTime = System.currentTimeMillis() - startTime;
            log.error("⚠️ [ ⬅ Outgoing ] Exception in Controller [{}] after {}ms: {}",
                    methodName, executionTime, ex.getMessage());
            throw ex;
        }

        // Log outgoing response with execution time
        long executionTime = System.currentTimeMillis() - startTime;
        log.info("✅ [ ⬅ Outgoing ] Response [{}] in {}ms: {}",
                methodName, executionTime, result);

        return result;
    }
}
