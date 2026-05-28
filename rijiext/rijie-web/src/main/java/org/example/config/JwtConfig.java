package org.example.config;

import jakarta.annotation.PostConstruct;
import org.example.utils.JwtUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

@Configuration
public class JwtConfig {

    @Value("${jwt.secret:}")
    private String jwtSecret;

    @PostConstruct
    public void initJwtKey() {
        JwtUtils.initSecretKey(jwtSecret);
    }
}
