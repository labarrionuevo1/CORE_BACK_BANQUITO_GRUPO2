package com.banquito.core.security.config;

import com.banquito.core.security.exception.AccessDeniedExceptionHandler;
import com.banquito.core.security.filter.BranchScopeFilter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    private final AccessDeniedExceptionHandler accessDeniedExceptionHandler;
    private final BranchScopeFilter branchScopeFilter;

    public SecurityConfig(AccessDeniedExceptionHandler accessDeniedExceptionHandler,
                          BranchScopeFilter branchScopeFilter) {
        this.accessDeniedExceptionHandler = accessDeniedExceptionHandler;
        this.branchScopeFilter = branchScopeFilter;
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .csrf(AbstractHttpConfigurer::disable)
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers(
                                "/swagger-ui/**",
                                "/swagger-ui.html",
                                "/v3/api-docs/**"
                        ).permitAll()
                        .anyRequest().permitAll()
                )
                .exceptionHandling(exception -> exception
                        .accessDeniedHandler(accessDeniedExceptionHandler)
                );

        return http.build();
    }
}
