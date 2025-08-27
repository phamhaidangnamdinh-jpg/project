package com.Chicken.project.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.NoOpPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity
public class SecurityConfig {
    private static final Logger log = LoggerFactory.getLogger(SecurityConfig.class);
    @Autowired
    private UserDetailsService userDetailsService;
    @Autowired
    private JwtFilter jwtFilter;
    @Bean
    public PasswordEncoder passwordEncoder() {
        log.info("Using BCryptPasswordEncoder with strength 12");
        return new BCryptPasswordEncoder(12);
    }
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http, RestAuthenticationEntryPoint authenticationEntryPoint
    , RestAccessDeniedHandler restAccessDeniedHandler) throws Exception {
        log.info("Initializing SecurityFilterChain");

        http.setSharedObject(AuthenticationEntryPoint.class, authenticationEntryPoint);
        http.setSharedObject(AccessDeniedHandler.class, restAccessDeniedHandler);

        log.info("Setting security rules: public endpoints and JWT filter");
        return http
                .csrf(customizer -> customizer.disable())
                .authorizeHttpRequests(request -> request
                        .requestMatchers("/api/v1/library/user/register", "/api/v1/library/user/login", "/api/v1/library/auth/register",
                                "/api/v1/library/auth/login", "/api/v1/library/auth/refresh",
                                "/swagger-ui.html",
                                "/swagger-ui/**",
                                "/v3/api-docs/**",
                                "/swagger-resources/**")
                        .permitAll()
                        .anyRequest().authenticated())
                .httpBasic(Customizer.withDefaults())
                .exceptionHandling(exception -> exception
                        .authenticationEntryPoint(authenticationEntryPoint)
                        .accessDeniedHandler(restAccessDeniedHandler))
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .addFilterBefore(jwtFilter, UsernamePasswordAuthenticationFilter.class)
                .build();
    }
    @Bean
    public AuthenticationProvider authenticationProvider() {
        log.info("Configuring DaoAuthenticationProvider with custom UserDetailsService");
        DaoAuthenticationProvider provider = new DaoAuthenticationProvider();
        provider.setPasswordEncoder(passwordEncoder());
        provider.setUserDetailsService(userDetailsService);
        return provider;
    }
    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration config) throws Exception {
            log.info("Creating AuthenticationManager from AuthenticationConfiguration");
            return config.getAuthenticationManager();
    }
}
