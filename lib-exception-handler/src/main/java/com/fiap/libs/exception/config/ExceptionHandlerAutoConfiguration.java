package com.fiap.libs.exception.config;

import com.fiap.libs.exception.handler.ClientErrorHandler;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication;
import org.springframework.context.annotation.Import;

@AutoConfiguration
@ConditionalOnWebApplication(type = ConditionalOnWebApplication.Type.SERVLET)
@Import({ClientErrorHandler.class})
public class ExceptionHandlerAutoConfiguration { }


