package com.chaewsstore.config.security;

import com.chaewsstore.common.security.filter.JwtAuthenticationFilter;
import com.chaewsstore.common.security.filter.JwtExceptionFilter;
import com.chaewsstore.core.infra.jwt.TokenProvider;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@RequiredArgsConstructor(access = AccessLevel.PROTECTED)
@Configuration
public class SecurityFilterConfig {

    private final ObjectMapper objectMapper;
    private final TokenProvider tokenProvider;
    private final RequestMatcherHolder requestMatcherHolder;

    @Bean
    public JwtExceptionFilter jwtExceptionFilter() {
        return new JwtExceptionFilter(objectMapper);
    }

    @Bean
    public JwtAuthenticationFilter jwtAuthorizationFilter() {
        return new JwtAuthenticationFilter(tokenProvider, requestMatcherHolder);
    }
}
