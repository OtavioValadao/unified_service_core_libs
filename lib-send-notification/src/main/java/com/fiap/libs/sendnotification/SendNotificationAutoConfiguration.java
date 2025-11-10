package com.fiap.libs.sendnotification;

import com.fiap.libs.sendnotification.config.NotificationProperties;
import com.fiap.libs.sendnotification.email.SendEmailNotification;
import com.fiap.libs.sendnotification.email.config.LoadTemplateConfig;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.core.io.ResourceLoader;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.JavaMailSenderImpl;

import java.util.Properties;

/**
 * Auto-configuration for notification services.
 * <p>
 * This configuration is automatically enabled when the library is on the classpath.
 * It can be disabled by setting: notification.enabled=false
 * <p>
 * For complete configuration options, see {@link NotificationProperties}.
 */
@AutoConfiguration
@EnableConfigurationProperties(NotificationProperties.class)
@ConditionalOnProperty(prefix = "notification", name = "enabled", havingValue = "true", matchIfMissing = true)
@Slf4j
public class SendNotificationAutoConfiguration {

    /**
     * Creates JavaMailSender bean if not already present.
     * Uses configuration from NotificationProperties.
     */
    @Bean
    @ConditionalOnMissingBean
    public JavaMailSender javaMailSender(NotificationProperties properties) {
        NotificationProperties.MailConfig config = properties.getMail();

        JavaMailSenderImpl mailSender = new JavaMailSenderImpl();
        mailSender.setHost(config.getHost());
        mailSender.setPort(config.getPort());
        mailSender.setProtocol(config.getProtocol());
        mailSender.setDefaultEncoding(config.getDefaultEncoding());

        if (config.getUsername() != null) {
            mailSender.setUsername(config.getUsername());
        }

        if (config.getPassword() != null) {
            mailSender.setPassword(config.getPassword());
        }

        Properties props = mailSender.getJavaMailProperties();
        config.getProperties().forEach(props::setProperty);

        log.info("✅ JavaMailSender configured - Host: {}, Port: {}", config.getHost(), config.getPort());

        return mailSender;
    }

    @Bean
    @ConditionalOnMissingBean
    public LoadTemplateConfig loadTemplateConfig(ResourceLoader resourceLoader) {
        return new LoadTemplateConfig(resourceLoader);
    }

    @Bean
    @ConditionalOnMissingBean
    public SendEmailNotification sendEmailNotification(
            JavaMailSender mailSender,
            LoadTemplateConfig loadTemplateConfig,
            NotificationProperties properties) {
        log.info("🚀 [SEND-NOTIFICATION] SendEmailNotification bean created successfully");
        return new SendEmailNotification(mailSender, loadTemplateConfig, properties);
    }

}