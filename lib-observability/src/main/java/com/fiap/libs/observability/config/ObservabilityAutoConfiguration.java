package com.fiap.libs.observability.config;

import com.fiap.libs.observability.aspect.HttpLoggingAspect;
import com.fiap.libs.observability.aspect.OperationLoggingAspect;
import com.fiap.libs.observability.utils.LoggingUtils;
import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.EnableAspectJAutoProxy;

/**
 * Auto-configuration for FIAP Observability Library.
 *
 * <p>Architecture-agnostic logging solution that works with:</p>
 * <ul>
 *   <li>Layered Architecture</li>
 *   <li>Hexagonal Architecture (Ports & Adapters)</li>
 *   <li>Clean Architecture</li>
 *   <li>Onion Architecture</li>
 *   <li>Domain-Driven Design</li>
 * </ul>
 *
 * @author FIAP
 * @since 2.0.0
 */
@AutoConfiguration
@EnableAspectJAutoProxy
@ConditionalOnClass(name = "org.aspectj.lang.annotation.Aspect")
@ConditionalOnProperty(prefix = "observability", name = "enabled", havingValue = "true", matchIfMissing = true)
@Slf4j
public class ObservabilityAutoConfiguration {

    @Value("${observability.log.json-pretty-print:false}")
    private boolean jsonPrettyPrintEnabled;

    @PostConstruct
    public void init() {
        LoggingUtils.setPrettyPrintEnabled(jsonPrettyPrintEnabled);
    }

    @Bean
    @ConditionalOnProperty(prefix = "observability.operation", name = "enabled", havingValue = "true", matchIfMissing = true)
    public OperationLoggingAspect operationLoggingAspect() {
        log.info("✓ [OBSERVABILITY] OperationLoggingAspect enabled - Use @LogOperation on business methods");
        return new OperationLoggingAspect();
    }

    @Bean
    @ConditionalOnProperty(prefix = "observability.http", name = "enabled", havingValue = "true", matchIfMissing = true)
    public HttpLoggingAspect httpLoggingAspect() {
        log.info("✓ [OBSERVABILITY] HttpLoggingAspect enabled - Use @LogHttp on HTTP entry points");
        return new HttpLoggingAspect();
    }
}