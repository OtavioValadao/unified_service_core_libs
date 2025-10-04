package com.fiap.libs.observability.aspect;

import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import java.util.Objects;

/**
 * Aspect for logging HTTP controller requests and responses.
 * <p>
 * Automatically logs all incoming HTTP requests and outgoing responses,
 * including method, path, parameters, and execution time.
 * </p>
 * <p>
 * Can be configured via properties to target specific base packages.
 * </p>
 *
 * @author FIAP Unified Service Core
 * @since 1.0.0
 */
@Aspect
@Component
@Slf4j
public class ControllerLoggingAspect {

    @Value("${observability.controller.base-package:**.controller..*}")
    private String basePackage;

    /**
     * Logs all controller method invocations within the configured base package.
     * Default pattern matches any package containing 'controller'.
     */
    @Around("execution(* " + "${observability.controller.base-package:**.controller..*}" + "(..))")
    public Object logController(ProceedingJoinPoint joinPoint) throws Throwable {

        HttpServletRequest request = ((ServletRequestAttributes) Objects.requireNonNull(
                RequestContextHolder.getRequestAttributes()
        )).getRequest();

        String methodName = request.getMethod();
        String fullPath = request.getRequestURI();
        String query = request.getQueryString();
        if (query != null) {
            fullPath += "?" + query;
        }

        String controllerMethod = joinPoint.getSignature().getName();
        Object[] args = joinPoint.getArgs();

        log.info("🔗 [ ⯈ Incoming ] HTTP method: {} - endpoint: {} - Controller [{}] args: {}",
                methodName, fullPath, controllerMethod, args);

        long startTime = System.currentTimeMillis();
        Object result;

        try {
            result = joinPoint.proceed();
        } catch (Throwable ex) {
            long executionTime = System.currentTimeMillis() - startTime;
            log.error("⚠️ [ ⬅ Outgoing ] Exception in Controller [{}] after {}ms: {}",
                    methodName, executionTime, ex.getMessage());
            throw ex;
        }

        long executionTime = System.currentTimeMillis() - startTime;
        log.info("✓ [ ⬅ Outgoing ] Response [{}] in {}ms: {}",
                methodName, executionTime, result);

        return result;
    }
}