package com.fiap.libs.observability.config;

import com.fiap.libs.observability.aspect.ControllerLoggingAspect;
import com.fiap.libs.observability.aspect.ServiceLoggingAspect;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.EnableAspectJAutoProxy;
import org.springframework.context.annotation.Lazy;

/**
 * Auto-configuration for Observability Library.
 * <p>
 * Automatically enables logging aspects when the library is present on the classpath.
 * Can be disabled via property: observability.enabled=false
 * </p>
 *
 * @author FIAP Unified Service Core
 * @since 1.0.0
 */
@AutoConfiguration
@EnableAspectJAutoProxy
@ConditionalOnClass(name = "org.aspectj.lang.annotation.Aspect")
@ConditionalOnProperty(prefix = "observability", name = "enabled", havingValue = "true", matchIfMissing = true)
@Slf4j
public class ObservabilityAutoConfiguration {

    public ObservabilityAutoConfiguration() {
        log.info("🔍 [OBSERVABILITY] Initializing FIAP Observability Library v1.0.0");
    }

    @Bean
    @ConditionalOnProperty(prefix = "observability.service", name = "enabled", havingValue = "true", matchIfMissing = true)
    public ServiceLoggingAspect serviceLoggingAspect() {
        log.info("✓ [OBSERVABILITY] ServiceLoggingAspect enabled - Use @StepLog on service methods");
        return new ServiceLoggingAspect();
    }

    @Bean
    @ConditionalOnProperty(prefix = "observability.controller", name = "enabled", havingValue = "true", matchIfMissing = true)
    @Lazy
    public ControllerLoggingAspect controllerLoggingAspect() {
        log.info("✓ [OBSERVABILITY] ControllerLoggingAspect enabled - Logging all controller requests");
        return new ControllerLoggingAspect();
    }
}