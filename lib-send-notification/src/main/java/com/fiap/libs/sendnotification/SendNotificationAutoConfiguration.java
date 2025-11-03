package com.fiap.libs.sendnotification;

import com.fiap.libs.sendnotification.email.SendEmailNotification;
import com.fiap.libs.sendnotification.email.config.LoadTemplateConfig;
import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.core.io.ResourceLoader;
import org.springframework.mail.javamail.JavaMailSender;

@AutoConfiguration
@Slf4j
public class SendNotificationAutoConfiguration {

    @PostConstruct
    public void init() {
        log.info("🚀 [SEND-NOTIFICATION] Auto-configuration initialized successfully.");
    }

    @Bean
    @ConditionalOnMissingBean
    public LoadTemplateConfig loadTemplateConfig(ResourceLoader resourceLoader) {
        log.debug("Creating LoadTemplateConfig bean");
        return new LoadTemplateConfig(resourceLoader);
    }

    @Bean
    @ConditionalOnMissingBean
    @ConditionalOnBean(JavaMailSender.class)
    public SendEmailNotification sendEmailNotification(
            JavaMailSender mailSender,
            LoadTemplateConfig loadTemplateConfig) {
        log.debug("Creating SendEmailNotification bean");
        return new SendEmailNotification(mailSender, loadTemplateConfig);
    }

}