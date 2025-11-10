package com.fiap.libs.pdfgenerator;

import com.fiap.libs.pdfgenerator.generator.PdfGenerator;
import com.fiap.libs.pdfgenerator.generator.PdfGeneratorProperties;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.core.io.ResourceLoader;

@Slf4j
@AutoConfiguration
@EnableConfigurationProperties(PdfGeneratorProperties.class)
@ConditionalOnProperty(prefix = "pdfgenerator", name = "enabled", havingValue = "true", matchIfMissing = true)
public class PdfGeneratorAutoConfiguration {

    @Bean
    public PdfGenerator pdfGenerator(PdfGeneratorProperties properties,
                                     ResourceLoader resourceLoader) {
        log.info("🧾 [PDF] PdfGenerator auto-configured (template: {})", properties.getTemplateLocation());
        return new PdfGenerator(resourceLoader, properties);
    }
}