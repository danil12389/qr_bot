package com.example.demo.config;

import org.apache.logging.log4j.core.config.plugins.validation.constraints.ValidHost;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;

@Configuration
@PropertySource("application.properties")
public class VkConfig {


    @Value("${bot.name}")
    String botName;

    @Value("${bot.key}")
    String token;

    @Value("${bot.id}")
    Integer id;

    public String getBotName() {
        return botName;
    }

    public String getToken() {
        return token;
    }

    public Integer getId() {
        return id;
    }
}
