package com.bookstore.web.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.NoOpPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import com.bookstore.web.security.JwtAuthenticationFilter;

/**
 * Spring Security configuration for the Bookstore web application.
 * Configures JWT authentication, public endpoints, and security filters.
 * 
 * @author Bookstore Team
 * @version 1.0
 */
@Configuration
@EnableWebSecurity
@EnableMethodSecurity(prePostEnabled = true)
public class SecurityConfig {
    
    private static final Logger LOGGER = LoggerFactory.getLogger(SecurityConfig.class);
    
    private final JwtAuthenticationFilter jwtAuthenticationFilter;
    
    @Autowired
    public SecurityConfig(JwtAuthenticationFilter jwtAuthenticationFilter) {
        this.jwtAuthenticationFilter = jwtAuthenticationFilter;
        LOGGER.info("SecurityConfig initialized with JwtAuthenticationFilter: {}", 
                   jwtAuthenticationFilter != null ? "present" : "null");
    }
    
    /**
     * Create password encoder bean.
     * 
     * @return the password encoder
     */
    @Bean
    public PasswordEncoder passwordEncoder() {
        LOGGER.info("Creating PasswordEncoder bean");
        return NoOpPasswordEncoder.getInstance();
    }
    
    /**
     * Create authentication manager bean.
     * 
     * @param authConfig the authentication configuration
     * @return the authentication manager
     * @throws Exception if an error occurs
     */
    @Bean
    public AuthenticationManager authenticationManager(
            final AuthenticationConfiguration authConfig) throws Exception {
        LOGGER.info("Creating AuthenticationManager bean");
        return authConfig.getAuthenticationManager();
    }
    
    /**
     * Configure security filter chain.
     * 
     * @param http the HTTP security configuration
     * @return the security filter chain
     * @throws Exception if an error occurs
     */
    @Bean
    public SecurityFilterChain filterChain(final HttpSecurity http) throws Exception {
        
        LOGGER.info("Configuring SecurityFilterChain");
        
        // Disable CSRF and configure session management for stateless JWT authentication
        http
            .csrf(csrf -> csrf.disable())
            .sessionManagement(session -> session
                    .sessionCreationPolicy(SessionCreationPolicy.STATELESS))
            .authorizeHttpRequests(auth -> auth
                    .requestMatchers(
                        "/api/auth/login",
                        "/api/auth/register",
                        "/swagger-ui/**",
                        "/v3/api-docs/**",
                        "/swagger-resources/**",
                        "/webjars/**"
                    ).permitAll()
                    .anyRequest().authenticated()
            );
        
        // Add JWT filter before UsernamePasswordAuthenticationFilter
        http.addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);
        
        LOGGER.info("JwtAuthenticationFilter added to security chain");
        LOGGER.info("SecurityFilterChain configuration completed");
        
        return http.build();
    }
}