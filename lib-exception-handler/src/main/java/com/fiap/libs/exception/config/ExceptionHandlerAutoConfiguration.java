package com.fiap.libs.exception.config;

import com.fiap.libs.exception.core.handler.ClientErrorHandler;
import com.fiap.libs.exception.core.registry.ExceptionMetadataRegistry;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication;
import org.springframework.context.annotation.Import;

/**
 * Auto-configuração da biblioteca de tratamento de exceções
 * Ativa automaticamente quando adicionada ao classpath
 */
@AutoConfiguration
@ConditionalOnWebApplication(type = ConditionalOnWebApplication.Type.SERVLET)
@Import({
        ExceptionMetadataRegistry.class,  // ← Registra primeiro o Registry
        ClientErrorHandler.class          // ← Depois o Handler (que depende do Registry)
})
public class ExceptionHandlerAutoConfiguration {
    // Configuração automática - sem código necessário
}