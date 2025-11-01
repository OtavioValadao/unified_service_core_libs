package com.fiap.libs.exception.config;

import com.fiap.libs.exception.core.handler.ClientErrorHandler;
import com.fiap.libs.exception.core.registry.ExceptionMetadataRegistry;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication;
import org.springframework.context.annotation.Import;

/**
 * Auto-configuração da biblioteca de tratamento de exceções
 * Ativa automaticamente quando adicionada ao classpath
 */
@AutoConfiguration
@Slf4j
@ConditionalOnWebApplication(type = ConditionalOnWebApplication.Type.SERVLET)
@Import({
        ExceptionMetadataRegistry.class,
        ClientErrorHandler.class
})
public class ExceptionHandlerAutoConfiguration {

    public ExceptionHandlerAutoConfiguration() {
        log.info("🔍 [EXCEPTION-HANDLER] Initializing FIAP handler Library");
    }

}