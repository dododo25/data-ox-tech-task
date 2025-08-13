package com.dododo.dataox.admin.config;

import com.dododo.dataox.admin.filter.BearerTokenAuthFilter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.www.BasicAuthenticationFilter;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Autowired
    private BearerTokenAuthFilter bearerTokenAuthFilter;

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        return http
                .authorizeHttpRequests(request -> {
                    request.requestMatchers("/login", "/swagger-ui/**", "/v3/api-docs/**").permitAll();
                    request.anyRequest()
                            .authenticated();
                })
                .csrf(AbstractHttpConfigurer::disable)
                .addFilterAfter(bearerTokenAuthFilter, BasicAuthenticationFilter.class)
                .build();
    }
}