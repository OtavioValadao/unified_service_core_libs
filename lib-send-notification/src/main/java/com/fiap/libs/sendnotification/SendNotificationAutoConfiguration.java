package com.fiap.libs.sendnotification;

import com.fiap.libs.sendnotification.email.SendEmailNotification;
import com.fiap.libs.sendnotification.email.config.LoadTemplateConfig;
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


    @Bean
    @ConditionalOnMissingBean
    public LoadTemplateConfig loadTemplateConfig(ResourceLoader resourceLoader) {
        return new LoadTemplateConfig(resourceLoader);
    }

    @Bean
    @ConditionalOnMissingBean
    @ConditionalOnBean(JavaMailSender.class)
    public SendEmailNotification sendEmailNotification(
            JavaMailSender mailSender,
            LoadTemplateConfig loadTemplateConfig) {
        log.info("🚀 [SEND-NOTIFICATION] Auto-configuration initialized successfully.");
        return new SendEmailNotification(mailSender, loadTemplateConfig);
    }

}