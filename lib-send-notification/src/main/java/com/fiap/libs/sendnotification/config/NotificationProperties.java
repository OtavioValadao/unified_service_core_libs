package com.fiap.libs.sendnotification.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

import java.util.HashMap;
import java.util.Map;

/**
 * Configuration properties for email notifications.
 *
 * <p>Example usage in application.yml:</p>
 * <pre>
 * notification:
 *   mail:
 *     host: smtp.gmail.com
 *     port: 587
 *     username: your-email@gmail.com
 *     password: your-password
 *     from:
 *       address: noreply@example.com
 *       name: My Application
 *     templates:
 *       welcome:
 *         path: template/email_welcome_content.html
 *         subject: Welcome!
 *       service-order-finalized:
 *         path: template/service_order_finalized_email.html
 *         subject: Your order is ready!
 * </pre>
 */
@Data
@ConfigurationProperties(prefix = "notification")
public class NotificationProperties {

    private MailConfig mail = new MailConfig();

    @Data
    public static class MailConfig {
        /**
         * SMTP server host. Default: smtp.gmail.com
         */
        private String host = "smtp.gmail.com";

        /**
         * SMTP server port. Default: 587
         */
        private Integer port = 587;

        /**
         * Login user of the SMTP server.
         */
        private String username;

        /**
         * Login password of the SMTP server.
         */
        private String password;

        /**
         * Protocol used by the SMTP server. Default: smtp
         */
        private String protocol = "smtp";

        /**
         * Default MimeMessage encoding. Default: UTF-8
         */
        private String defaultEncoding = "UTF-8";

        /**
         * Additional JavaMail Session properties.
         */
        private Map<String, String> properties = new HashMap<>() {{
            put("mail.smtp.auth", "true");
            put("mail.smtp.starttls.enable", "true");
            put("mail.smtp.starttls.required", "true");
            put("mail.smtp.ssl.trust", "*");
        }};

        /**
         * Email sender configuration.
         */
        private From from = new From();

        /**
         * Email templates configuration.
         */
        private Templates templates = new Templates();

        @Data
        public static class From {
            /**
             * Email address to use as sender. Default: noreply@example.com
             */
            private String address = "noreply@example.com";

            /**
             * Name to display as sender. Default: Notification Service
             */
            private String name = "Notification Service";
        }

        @Data
        public static class Templates {
            /**
             * Welcome email template configuration.
             */
            private EmailTemplate welcome = new EmailTemplate(
                    "template/email_welcome_content.html",
                    "Welcome!"
            );

            /**
             * Service order finalized email template configuration.
             */
            private EmailTemplate serviceOrderFinalized = new EmailTemplate(
                    "template/service_order_finalized_email.html",
                    "Your order is ready for pickup!"
            );

            @Data
            public static class EmailTemplate {
                /**
                 * Path to the template file (classpath resource).
                 */
                private String path;

                /**
                 * Email subject.
                 */
                private String subject;

                public EmailTemplate() {
                }

                public EmailTemplate(String path, String subject) {
                    this.path = path;
                    this.subject = subject;
                }
            }
        }
    }
}