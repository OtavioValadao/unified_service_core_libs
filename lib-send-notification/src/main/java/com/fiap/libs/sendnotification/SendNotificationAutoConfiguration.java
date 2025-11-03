package com.fiap.libs.sendnotification;

import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.context.annotation.ComponentScan;

@AutoConfiguration
@ComponentScan("com.fiap.libs.sendnotification")
@Slf4j
public class SendNotificationAutoConfiguration {



    @PostConstruct
    public void init() {
        log.info("🚀 [SEND-NOTIFICATION] Auto-configuration initialized successfully.");
    }

}