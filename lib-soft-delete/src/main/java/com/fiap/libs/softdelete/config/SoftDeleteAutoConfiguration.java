package com.fiap.libs.softdelete.config;

import com.fiap.libs.softdelete.SoftDeleteHelper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.context.annotation.Bean;

/**
 * Auto-configuração da biblioteca de Soft Delete.
 * Ativa automaticamente quando adicionada ao classpath.
 * 
 * <p>Registra o bean {@link SoftDeleteHelper} para uso em toda a aplicação.
 *
 * @author FIAP - Unified Service Core Team
 * @since 1.5.3
 */
@AutoConfiguration
@Slf4j
public class SoftDeleteAutoConfiguration {

    public SoftDeleteAutoConfiguration() {
        log.info("🗑️ [SOFT-DELETE] Initializing FIAP Soft Delete Library");
    }

    @Bean
    public SoftDeleteHelper softDeleteHelper() {
        log.info("✓ [SOFT-DELETE] SoftDeleteHelper bean registered");
        return new SoftDeleteHelper();
    }
}

