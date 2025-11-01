
package com.fiap.libs.sendnotification.email.config;

import lombok.experimental.UtilityClass;

@UtilityClass
public class MailProperties {

    public static final String FROM_NAME = "Unified Service Core";

    public static final String FROM_ADDRESS = "unifiedservicecore@gmail.com";

    public static final String WELCOME_SUBJECT = "Seja muito bem vindo!!!!!";

    public static final String WELCOME_TEMPLATE_PATH = "template/email_welcome_content.html";

    public static final String FINALIZE_TEMPLATE_PATH = "template/service_order_finalized_email.html";

    public static final String FINALIZE_SUBJECT = "Sua OS está pronta para retirada!";

    public static final String UTF_8 = "UTF-8";

    public static final String CLIENT = "{{cliente}}";
}

