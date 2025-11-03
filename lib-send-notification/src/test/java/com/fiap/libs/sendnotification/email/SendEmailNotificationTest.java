package com.fiap.libs.sendnotification.email;

import com.fiap.libs.sendnotification.config.NotificationProperties;
import com.fiap.libs.sendnotification.email.config.LoadTemplateConfig;
import com.fiap.libs.sendnotification.email.dto.CustomerRecord;
import com.fiap.libs.sendnotification.email.dto.ModelRecord;
import com.fiap.libs.sendnotification.email.dto.ServiceOrderRecord;
import com.fiap.libs.sendnotification.email.dto.VehicleRecord;
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
    private NotificationProperties properties;

    @BeforeEach
    void setUp() {
        properties = new NotificationProperties();
        sendEmailNotification = new SendEmailNotification(mailSender, loadTemplateConfig, properties);
        when(mailSender.createMimeMessage()).thenReturn(new MimeMessage((Session) null));
    }

    @Test
    void shouldSendWelcomeEmail_successfully() throws Exception {
        // Given
        CustomerRecord client = new CustomerRecord(
                "John Doe",
                "test@example.com"
        );

        String template = "<html><body>Bem-vindo {{cliente}}!</body></html>";
        when(loadTemplateConfig.loadTemplate(properties.getMail().getTemplates().getWelcome().getPath())).thenReturn(template);

        // When
        sendEmailNotification.sendEmailWelcome(client);

        // Give some time for async execution
        Thread.sleep(100);

        // Then
        verify(mailSender, times(1)).send(any(MimeMessage.class));
        verify(loadTemplateConfig, times(1)).loadTemplate(properties.getMail().getTemplates().getWelcome().getPath());
    }

    @Test
    void shouldSendServiceOrderFinalizedEmail_successfully() throws Exception {
        // Given
        CustomerRecord client = new CustomerRecord(
                "Jane Smith",
                "client@example.com"
        );

        ModelRecord model = new ModelRecord(
                2023,
                "Corolla",
                "Toyota"
        );

        VehicleRecord vehicle = new VehicleRecord(
                "ABC-1234",
                model
        );

        ServiceOrderRecord serviceOrder = new ServiceOrderRecord(
                "OS-12345",
                client,
                vehicle,
                "2025-01-15"
        );

        String template = "<html><body>OS {{osNumero}} finalizada para {{cliente}}. Veículo: {{veiculo}}. Data: {{dataFinalizacao}}</body></html>";
        when(loadTemplateConfig.loadTemplate(properties.getMail().getTemplates().getServiceOrderFinalized().getPath())).thenReturn(template);

        // When
        sendEmailNotification.sendServiceOrderFinalizedEmail(serviceOrder);

        // Give some time for async execution
        Thread.sleep(100);

        // Then
        verify(mailSender, times(1)).send(any(MimeMessage.class));
        verify(loadTemplateConfig, times(1)).loadTemplate(properties.getMail().getTemplates().getServiceOrderFinalized().getPath());
    }

    @Test
    void shouldHandleInvalidEmailGracefully() throws Exception {
        // Given
        CustomerRecord clientWithInvalidEmail = new CustomerRecord(
                "Invalid User",
                "invalid-email"
        );

        String template = "<html><body>Bem-vindo {{cliente}}!</body></html>";
        when(loadTemplateConfig.loadTemplate(properties.getMail().getTemplates().getWelcome().getPath())).thenReturn(template);

        // When / Then - Should not throw exception, just log error
        assertThatCode(() -> sendEmailNotification.sendEmailWelcome(clientWithInvalidEmail))
                .doesNotThrowAnyException();

        // Give some time for async execution
        Thread.sleep(100);
    }

    @Test
    void shouldLoadTemplate_whenSendingEmail() throws Exception {
        // Given
        CustomerRecord client = new CustomerRecord(
                "Template Test",
                "template@example.com"
        );

        String template = "<html><body>Olá {{cliente}}, seja bem-vindo!</body></html>";
        when(loadTemplateConfig.loadTemplate(properties.getMail().getTemplates().getWelcome().getPath())).thenReturn(template);

        // When
        sendEmailNotification.sendEmailWelcome(client);

        // Give some time for async execution
        Thread.sleep(100);

        // Then
        verify(loadTemplateConfig, times(1)).loadTemplate(properties.getMail().getTemplates().getWelcome().getPath());
    }

    @Test
    void shouldHandleTemplateLoadingException() throws Exception {
        // Given
        CustomerRecord client = new CustomerRecord(
                "Test User",
                "test@example.com"
        );

        when(loadTemplateConfig.loadTemplate(properties.getMail().getTemplates().getWelcome().getPath()))
                .thenThrow(new RuntimeException("Template not found"));

        // When / Then - Should not throw exception, just log error
        assertThatCode(() -> sendEmailNotification.sendEmailWelcome(client))
                .doesNotThrowAnyException();

        // Give some time for async execution
        Thread.sleep(100);
    }
}
