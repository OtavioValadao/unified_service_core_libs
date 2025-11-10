package com.fiap.libs.pdfgenerator.generator;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.sf.jasperreports.engine.*;
import net.sf.jasperreports.engine.data.JRBeanCollectionDataSource;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.stereotype.Component;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.attribute.PosixFilePermissions;
import java.util.*;

@Slf4j
@Component
@RequiredArgsConstructor
public class PdfGenerator {

    private final ResourceLoader resourceLoader;
    private final PdfGeneratorProperties properties;

    private static final String PREFIX_OS = "os_";
    private static final String SUFFIX_OS = ".pdf";

    public File generatePdfFile(List<Map<String, Object>> items, Map<String, Object> orderData)
            throws JRException, IOException {

        Objects.requireNonNull(orderData, "Order data cannot be null");
        Objects.requireNonNull(items, "Items cannot be null");

        String location = properties.getTemplateLocation();
        log.info("🧾 Using template from: {}", location);

        Resource resource = resourceLoader.getResource(location);
        if (!resource.exists()) {
            throw new JRException("Template not found at: " + location);
        }

        try (InputStream templateStream = resource.getInputStream()) {
            JasperReport jasperReport = JasperCompileManager.compileReport(templateStream);
            JRBeanCollectionDataSource dataSource = new JRBeanCollectionDataSource(items);

            JasperPrint jasperPrint = JasperFillManager.fillReport(jasperReport, new HashMap<>(orderData), dataSource);

            File pdf = createSecureTempFile();
            JasperExportManager.exportReportToPdfFile(jasperPrint, pdf.getAbsolutePath());

            log.info("📄 PDF successfully generated at: {}", pdf.getAbsolutePath());
            return pdf;
        }
    }

    private File createSecureTempFile() throws IOException {
        String uniqueFileName = PREFIX_OS + UUID.randomUUID() + SUFFIX_OS;
        Path tempDir;

        try {
            tempDir = Files.createTempDirectory(
                    "pdf_generator_",
                    PosixFilePermissions.asFileAttribute(PosixFilePermissions.fromString("rwx------"))
            );
        } catch (UnsupportedOperationException e) {
            tempDir = Files.createTempDirectory("pdf_generator_");
        }

        Path tempFile = tempDir.resolve(uniqueFileName);
        Files.createFile(tempFile);

        File pdfFile = tempFile.toFile();
        pdfFile.deleteOnExit();
        tempDir.toFile().deleteOnExit();

        return pdfFile;
    }
}
