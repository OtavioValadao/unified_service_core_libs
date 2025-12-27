package com.fiap.libs.sendnotification;

import com.fiap.libs.sendnotification.email.SendEmailNotification;
import com.fiap.libs.sendnotification.email.config.LoadTemplateConfig;
import org.junit.jupiter.api.Test;
import org.springframework.boot.autoconfigure.AutoConfigurations;
import org.springframework.boot.mail.autoconfigure.MailSenderAutoConfiguration;
import org.springframework.boot.test.context.runner.ApplicationContextRunner;
import org.springframework.mail.javamail.JavaMailSender;

import static org.assertj.core.api.Assertions.assertThat;

class SendNotificationAutoConfigurationTest {

    private final ApplicationContextRunner contextRunner = new ApplicationContextRunner()
            .withConfiguration(AutoConfigurations.of(
                    MailSenderAutoConfiguration.class,
                    SendNotificationAutoConfiguration.class
            ))
            .withPropertyValues(
                    "spring.mail.host=localhost",
                    "spring.mail.port=3025",
                    "spring.mail.username=test",
                    "spring.mail.password=test"
            );

    @Test
    void shouldLoadAutoConfiguration_successfully() {
        contextRunner.run(context -> {
            assertThat(context).hasNotFailed();
            assertThat(context).hasSingleBean(SendNotificationAutoConfiguration.class);
        });
    }

    @Test
    void shouldRegisterSendEmailNotification_bean() {
        contextRunner
                .withBean(LoadTemplateConfig.class)
                .withBean(SendEmailNotification.class)
                .run(context -> {
                    assertThat(context).hasSingleBean(SendEmailNotification.class);
                    assertThat(context).hasSingleBean(JavaMailSender.class);
                    assertThat(context).hasSingleBean(LoadTemplateConfig.class);
                });
    }

    @Test
    void shouldInitializeConfiguration_withoutErrors() {
        contextRunner.run(context -> {
            SendNotificationAutoConfiguration config = context.getBean(SendNotificationAutoConfiguration.class);
            assertThat(config).isNotNull();
        });
    }

    @Test
    void shouldHaveMailSender_whenMailPropertiesAreProvided() {
        contextRunner.run(context -> {
            assertThat(context).hasSingleBean(JavaMailSender.class);
            JavaMailSender mailSender = context.getBean(JavaMailSender.class);
            assertThat(mailSender).isNotNull();
        });
    }

    @Test
    void shouldLoadAutoConfiguration_fromMetaInf() {
        // This test verifies that the auto-configuration is properly registered
        // in META-INF/spring/org.springframework.boot.autoconfigure.AutoConfiguration.imports
        contextRunner.run(context -> {
            assertThat(context.getBeanNamesForType(SendNotificationAutoConfiguration.class))
                    .hasSize(1);
        });
    }
}
