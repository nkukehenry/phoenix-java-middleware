package com.interswitchug.phoenix.api.middleware.config;
import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.security.Security;

@Configuration
public class SecurityConfig {
    @Bean
    public BouncyCastleProvider bouncyCastleProvider() {
        Security.addProvider(new BouncyCastleProvider());
        return new BouncyCastleProvider();
    }
}