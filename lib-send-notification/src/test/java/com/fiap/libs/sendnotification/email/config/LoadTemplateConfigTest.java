package com.fiap.libs.sendnotification.email.config;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;

import java.io.ByteArrayInputStream;
import java.io.IOException;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class LoadTemplateConfigTest {

    @Mock
    private ResourceLoader resourceLoader;

    @Mock
    private Resource mockResource;

    private LoadTemplateConfig loadTemplateConfig;

    @BeforeEach
    void setUp() {
        loadTemplateConfig = new LoadTemplateConfig(resourceLoader);
    }

    @Test
    void shouldLoadTemplateSuccessfully_whenResourceExists() throws IOException {
        // Given
        String templatePath = "templates/test_template.html";
        String expectedContent = "<html><body>Test Template</body></html>";

        when(resourceLoader.getResource("classpath:" + templatePath)).thenReturn(mockResource);
        when(mockResource.exists()).thenReturn(true);
        when(mockResource.isReadable()).thenReturn(true);
        when(mockResource.getInputStream()).thenReturn(new ByteArrayInputStream(expectedContent.getBytes()));

        // When
        String result = loadTemplateConfig.loadTemplate(templatePath);

        // Then
        assertThat(result).isEqualTo(expectedContent);
    }

    @Test
    void shouldThrowException_whenTemplateNotFound() {
        // Given
        String templatePath = "templates/non_existent_template.html";

        when(resourceLoader.getResource(anyString())).thenReturn(mockResource);
        when(mockResource.exists()).thenReturn(false);

        // When / Then
        assertThatThrownBy(() -> loadTemplateConfig.loadTemplate(templatePath))
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("Template file does not exist");
    }

    @Test
    void shouldLoadRealTemplate_fromClasspath() {
        // Given
        LoadTemplateConfig realConfig = new LoadTemplateConfig(new org.springframework.core.io.DefaultResourceLoader());

        // When
        String result = realConfig.loadTemplate("template/email_welcome_content.html");

        // Then
        assertThat(result).isNotEmpty();
        assertThat(result).contains("{{cliente}}");
    }

    @Test
    void shouldHandleMultilineTemplate() throws IOException {
        // Given
        String templatePath = "templates/multiline_template.html";
        String multilineContent = "<html>\n<body>\n<h1>Hello</h1>\n</body>\n</html>";

        when(resourceLoader.getResource("classpath:" + templatePath)).thenReturn(mockResource);
        when(mockResource.exists()).thenReturn(true);
        when(mockResource.isReadable()).thenReturn(true);
        when(mockResource.getInputStream()).thenReturn(new ByteArrayInputStream(multilineContent.getBytes()));

        // When
        String result = loadTemplateConfig.loadTemplate(templatePath);

        // Then
        assertThat(result).contains("<html>");
        assertThat(result).contains("<body>");
        assertThat(result).contains("<h1>Hello</h1>");
    }
}
