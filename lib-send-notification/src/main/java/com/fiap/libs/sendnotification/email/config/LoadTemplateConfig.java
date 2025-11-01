package com.fiap.libs.sendnotification.email.config;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.stereotype.Service;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@Slf4j
@RequiredArgsConstructor
public class LoadTemplateConfig {

    private final ResourceLoader resourceLoader;

    public String loadTemplate(String path) {
        log.debug("Attempting to load template: {}", path);

        return tryLoadTemplateWithResourceLoader(path)
                .or(() -> tryLoadTemplateWithClassPathResource(path))
                .or(() -> tryLoadTemplateWithThreadClassLoader(path))
                .or(() -> tryLoadTemplateWithClassLoader(path))
                .orElseThrow(() -> {
                    log.error("Template file not found after trying all strategies: {}", path);
                    return new RuntimeException("Template file does not exist: " + path);
                });
    }

    private Optional<String> tryLoadTemplateWithResourceLoader(String path) {
        try {
            Resource resource = resourceLoader.getResource("classpath:" + path);
            if (resource.exists() && resource.isReadable()) {
                log.debug("Template found using ResourceLoader with classpath: prefix");
                return Optional.of(readResource(resource));
            }
        } catch (Exception e) {
            log.debug("Failed to load with ResourceLoader classpath: - {}", e.getMessage());
        }
        return Optional.empty();
    }

    private Optional<String> tryLoadTemplateWithClassPathResource(String path) {
        try {
            Resource resource = new ClassPathResource(path);
            if (resource.exists() && resource.isReadable()) {
                log.debug("Template found using ClassPathResource");
                return Optional.of(readResource(resource));
            }
        } catch (Exception e) {
            log.debug("Failed to load with ClassPathResource - {}", e.getMessage());
        }
        return Optional.empty();
    }

    private Optional<String> tryLoadTemplateWithThreadClassLoader(String path) {
        try {
            ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
            try (BufferedReader reader = new BufferedReader(
                    new InputStreamReader(Objects.requireNonNull(classLoader.getResourceAsStream(path)), StandardCharsets.UTF_8))) {
                String content = reader.lines().collect(Collectors.joining("\n"));
                log.debug("Template found using Thread ClassLoader");
                return Optional.of(content);
            }
        } catch (Exception e) {
            log.debug("Failed to load with Thread ClassLoader - {}", e.getMessage());
        }
        return Optional.empty();
    }

    private Optional<String> tryLoadTemplateWithClassLoader(String path) {
        try {
            try (BufferedReader reader = new BufferedReader(
                    new InputStreamReader(Objects.requireNonNull(getClass().getClassLoader().getResourceAsStream(path)), StandardCharsets.UTF_8))) {
                String content = reader.lines().collect(Collectors.joining("\n"));
                log.debug("Template found using Class ClassLoader");
                return java.util.Optional.of(content);
            }
        } catch (Exception e) {
            log.debug("Failed to load with Class ClassLoader - {}", e.getMessage());
        }
        return Optional.empty();
    }

    private String readResource(Resource resource) throws IOException {
        try (BufferedReader reader = new BufferedReader(
                new InputStreamReader(resource.getInputStream(), StandardCharsets.UTF_8))) {
            return reader.lines().collect(Collectors.joining("\n"));
        }
    }
}
