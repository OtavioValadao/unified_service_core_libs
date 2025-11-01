package com.fiap.libs.codegen.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.AutoConfiguration;

/**
 * Auto-configuração da biblioteca de geração de código
 * Ativa automaticamente quando adicionada ao classpath
 */
@AutoConfiguration
@Slf4j
public class CodeGenAutoConfiguration {

    public CodeGenAutoConfiguration() {
        log.info("📝 [CODE-GEN] Initializing FIAP Code Generation Library v1.0.15");
    }

}
