package com.fiap.libs.sendnotification.email;

import com.fiap.libs.observability.annotation.LogOperation;
import com.fiap.libs.sendnotification.config.NotificationProperties;
import com.fiap.libs.sendnotification.email.config.LoadTemplateConfig;
import com.fiap.libs.sendnotification.email.dto.CustomerRecord;
import com.fiap.libs.sendnotification.email.dto.ServiceOrderRecord;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;

import java.util.concurrent.CompletableFuture;

@Slf4j
@RequiredArgsConstructor
public class SendEmailNotification {

    private final JavaMailSender mailSender;
    private final LoadTemplateConfig loadTemplateConfig;
    private final NotificationProperties properties;

    @LogOperation("Send email notification welcome")
    public void sendEmailWelcome(CustomerRecord client) {
        CompletableFuture.runAsync(() -> {
            try {
                var welcomeConfig = properties.getMail().getTemplates().getWelcome();
                String template = loadTemplateConfig.loadTemplate(welcomeConfig.getPath());

                String htmlBody = template.replace("{{cliente}}", client.nickName());

                MimeMessage message = mailSender.createMimeMessage();
                MimeMessageHelper helper = new MimeMessageHelper(
                        message,
                        true,
                        properties.getMail().getDefaultEncoding()
                );

                helper.setFrom(
                        properties.getMail().getFrom().getAddress(),
                        properties.getMail().getFrom().getName()
                );
                helper.setTo(client.email());
                helper.setSubject(welcomeConfig.getSubject());
                helper.setText(htmlBody, true);

                mailSender.send(message);

                log.info("Welcome email sent successfully to {}", client.email());

            } catch (Exception e) {
                log.error("Error when send welcome email to {}: {}", client.nickName(), e.getMessage(), e);
            }
        });
    }

    @LogOperation("Send service order finalized email")
    public void sendServiceOrderFinalizedEmail(ServiceOrderRecord serviceOrder) {
        CompletableFuture.runAsync(() -> {
            try {
                var client = serviceOrder.client();
                var finalizedConfig = properties.getMail().getTemplates().getServiceOrderFinalized();

                String htmlBody = loadTemplateConfig.loadTemplate(finalizedConfig.getPath());

                String completionDate = serviceOrder.completionDate();

                String vehicleInfo = String.format("%s %s (%d) - Placa: %s",
                        serviceOrder.vehicleRecord().model().brand(),
                        serviceOrder.vehicleRecord().model().model(),
                        serviceOrder.vehicleRecord().model().year(),
                        serviceOrder.vehicleRecord().plate());

                htmlBody = htmlBody
                        .replace("{{cliente}}", client.nickName())
                        .replace("{{osNumero}}", serviceOrder.orderNumber())
                        .replace("{{veiculo}}", vehicleInfo)
                        .replace("{{dataFinalizacao}}", completionDate);

                MimeMessage message = mailSender.createMimeMessage();
                MimeMessageHelper helper = new MimeMessageHelper(
                        message,
                        true,
                        properties.getMail().getDefaultEncoding()
                );

                helper.setFrom(
                        properties.getMail().getFrom().getAddress(),
                        properties.getMail().getFrom().getName()
                );
                helper.setTo(serviceOrder.client().email());
                helper.setSubject(finalizedConfig.getSubject() + " - OS " + serviceOrder.orderNumber());
                helper.setText(htmlBody, true);

                mailSender.send(message);

                log.info("Finalized email sent successfully to {} for OS {}",
                        serviceOrder.client().email(),
                        serviceOrder.orderNumber());

            } catch (Exception e) {
                log.error("Error when sending Service Order finalized email for OS {}: {}",
                        serviceOrder.orderNumber(),
                        e.getMessage(),
                        e);
            }
        });
    }
}
