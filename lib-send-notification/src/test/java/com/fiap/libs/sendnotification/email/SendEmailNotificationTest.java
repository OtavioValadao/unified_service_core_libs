package com.fiap.libs.sendnotification.email;

import com.fiap.libs.sendnotification.email.config.LoadTemplateConfig;
import com.fiap.libs.sendnotification.email.dto.ClientDto;
import com.fiap.libs.sendnotification.email.dto.ModelDto;
import com.fiap.libs.sendnotification.email.dto.ServiceOrderDto;
import com.fiap.libs.sendnotification.email.dto.VehicleDto;
import jakarta.mail.Session;
import jakarta.mail.internet.MimeMessage;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;
import org.springframework.mail.javamail.JavaMailSender;

import static com.fiap.libs.sendnotification.email.config.MailProperties.*;
import static org.assertj.core.api.Assertions.assertThatCode;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
class SendEmailNotificationTest {

    @Mock
    private JavaMailSender mailSender;

    @Mock
    private LoadTemplateConfig loadTemplateConfig;

    private SendEmailNotification sendEmailNotification;

    @BeforeEach
    void setUp() {
        sendEmailNotification = new SendEmailNotification(mailSender, loadTemplateConfig);
        when(mailSender.createMimeMessage()).thenReturn(new MimeMessage((Session) null));
    }

    @Test
    void shouldSendWelcomeEmail_successfully() throws Exception {
        // Given
        ClientDto client = new ClientDto(
                "John Doe",
                "test@example.com"
        );

        String template = "<html><body>Bem-vindo {{cliente}}!</body></html>";
        when(loadTemplateConfig.loadTemplate(WELCOME_TEMPLATE_PATH)).thenReturn(template);

        // When
        sendEmailNotification.sendEmailWelcome(client);

        // Give some time for async execution
        Thread.sleep(100);

        // Then
        verify(mailSender, times(1)).send(any(MimeMessage.class));
        verify(loadTemplateConfig, times(1)).loadTemplate(WELCOME_TEMPLATE_PATH);
    }

    @Test
    void shouldSendServiceOrderFinalizedEmail_successfully() throws Exception {
        // Given
        ClientDto client = new ClientDto(
                "Jane Smith",
                "client@example.com"
        );

        ModelDto model = new ModelDto(
                2023,
                "Corolla",
                "Toyota"
        );

        VehicleDto vehicle = new VehicleDto(
                "ABC-1234",
                model
        );

        ServiceOrderDto serviceOrder = new ServiceOrderDto(
                "OS-12345",
                client,
                vehicle,
                "2025-01-15"
        );

        String template = "<html><body>OS {{osNumero}} finalizada para {{cliente}}. Veículo: {{veiculo}}. Data: {{dataFinalizacao}}</body></html>";
        when(loadTemplateConfig.loadTemplate(FINALIZE_TEMPLATE_PATH)).thenReturn(template);

        // When
        sendEmailNotification.sendServiceOrderFinalizedEmail(serviceOrder);

        // Give some time for async execution
        Thread.sleep(100);

        // Then
        verify(mailSender, times(1)).send(any(MimeMessage.class));
        verify(loadTemplateConfig, times(1)).loadTemplate(FINALIZE_TEMPLATE_PATH);
    }

    @Test
    void shouldHandleInvalidEmailGracefully() throws Exception {
        // Given
        ClientDto clientWithInvalidEmail = new ClientDto(
                "Invalid User",
                "invalid-email"
        );

        String template = "<html><body>Bem-vindo {{cliente}}!</body></html>";
        when(loadTemplateConfig.loadTemplate(WELCOME_TEMPLATE_PATH)).thenReturn(template);

        // When / Then - Should not throw exception, just log error
        assertThatCode(() -> sendEmailNotification.sendEmailWelcome(clientWithInvalidEmail))
                .doesNotThrowAnyException();

        // Give some time for async execution
        Thread.sleep(100);
    }

    @Test
    void shouldLoadTemplate_whenSendingEmail() throws Exception {
        // Given
        ClientDto client = new ClientDto(
                "Template Test",
                "template@example.com"
        );

        String template = "<html><body>Olá {{cliente}}, seja bem-vindo!</body></html>";
        when(loadTemplateConfig.loadTemplate(WELCOME_TEMPLATE_PATH)).thenReturn(template);

        // When
        sendEmailNotification.sendEmailWelcome(client);

        // Give some time for async execution
        Thread.sleep(100);

        // Then
        verify(loadTemplateConfig, times(1)).loadTemplate(WELCOME_TEMPLATE_PATH);
    }

    @Test
    void shouldHandleTemplateLoadingException() throws Exception {
        // Given
        ClientDto client = new ClientDto(
                "Test User",
                "test@example.com"
        );

        when(loadTemplateConfig.loadTemplate(WELCOME_TEMPLATE_PATH))
                .thenThrow(new RuntimeException("Template not found"));

        // When / Then - Should not throw exception, just log error
        assertThatCode(() -> sendEmailNotification.sendEmailWelcome(client))
                .doesNotThrowAnyException();

        // Give some time for async execution
        Thread.sleep(100);
    }
}
