package com.fiap.libs.sendnotification.email;

import com.fiap.libs.observability.annotation.LogOperation;
import com.fiap.libs.sendnotification.email.config.LoadTemplateConfig;
import com.fiap.libs.sendnotification.email.dto.ClientDto;
import com.fiap.libs.sendnotification.email.dto.ServiceOrderDto;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Component;

import java.util.concurrent.CompletableFuture;

import static com.fiap.libs.sendnotification.email.config.MailProperties.*;


@Component
@Slf4j
@RequiredArgsConstructor
public class SendEmailNotification {

    private final JavaMailSender mailSender;
    private final LoadTemplateConfig loadTemplateConfig;

    @LogOperation("Send email notification welcome")
    public void sendEmailWelcome(ClientDto client) {
        CompletableFuture.runAsync(() -> {
            try {
                String template = loadTemplateConfig.loadTemplate(WELCOME_TEMPLATE_PATH);

                String htmlBody = template
                        .replace(CLIENT, client.nickName());

                MimeMessage message = mailSender.createMimeMessage();
                MimeMessageHelper helper = new MimeMessageHelper(message, true, UTF_8);

                helper.setFrom(FROM_ADDRESS, FROM_NAME);
                helper.setTo(client.email());
                helper.setSubject(WELCOME_SUBJECT);
                helper.setText(htmlBody, true);

                mailSender.send(message);

                log.info("Welcome email sent successfully to {}", client.email());

            } catch (Exception e) {
                log.error("Error when send welcome email to {}: {}", client.nickName(), e.getMessage(), e);
            }
        });
    }

    @LogOperation("Send service order finalized email")
    public void sendServiceOrderFinalizedEmail(ServiceOrderDto serviceOrder) {
        CompletableFuture.runAsync(() -> {
            try {
                var client = serviceOrder.client();

                String htmlBody = loadTemplateConfig.loadTemplate(FINALIZE_TEMPLATE_PATH);

                String completionDate = (serviceOrder.completionDate());

                String vehicleInfo = String.format("%s %s (%d) - Placa: %s",
                        serviceOrder.vehicleDto().model().brand(),
                        serviceOrder.vehicleDto().model().model(),
                        serviceOrder.vehicleDto().model().year(),
                        serviceOrder.vehicleDto().plate());

                htmlBody = htmlBody
                        .replace(CLIENT, client.nickName())
                        .replace("{{osNumero}}", serviceOrder.orderNumber())
                        .replace("{{veiculo}}", vehicleInfo)
                        .replace("{{dataFinalizacao}}", completionDate);

                MimeMessage message = mailSender.createMimeMessage();
                MimeMessageHelper helper = new MimeMessageHelper(message, true, UTF_8);

                helper.setFrom(FROM_ADDRESS, FROM_NAME);
                helper.setTo(serviceOrder.client().email());
                helper.setSubject(FINALIZE_SUBJECT + " - OS " + serviceOrder.orderNumber());
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
