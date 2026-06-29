package com.back.global.app;

import com.back.standard.util.Ut;
import jakarta.annotation.PostConstruct;
import lombok.Getter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import tools.jackson.databind.ObjectMapper;

@Configuration
public class AppConfig {

    @Getter
    private static ObjectMapper objectMapper;

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Autowired
    public void setObjectMapper(ObjectMapper objectMapper) {
        AppConfig.objectMapper = objectMapper;
    }

    @PostConstruct
    public void postConstruct() {
        Ut.json.objectMapper = objectMapper;
    }
}
