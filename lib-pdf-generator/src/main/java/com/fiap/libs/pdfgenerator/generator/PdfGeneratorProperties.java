package com.fiap.libs.pdfgenerator.generator;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

@Data
@ConfigurationProperties(prefix = "pdfgenerator")
public class PdfGeneratorProperties {

    /**
     * Caminho do template (.jrxml).
     * Pode ser classpath: ou file:
     * Ex: classpath:templates/os-template.jrxml
     *     file:/opt/app/templates/os-template.jrxml
     */
    private String templateLocation = "classpath:templates/os-template.jrxml";
}
