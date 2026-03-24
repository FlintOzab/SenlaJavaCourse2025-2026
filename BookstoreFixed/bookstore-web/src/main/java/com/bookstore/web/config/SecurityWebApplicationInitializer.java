package com.bookstore.web.config;

import org.springframework.security.web.context.AbstractSecurityWebApplicationInitializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import jakarta.servlet.ServletContext;
import jakarta.servlet.ServletException;

/**
 * Security web application initializer for the Bookstore application.
 * Registers Spring Security filter chain and enables security for all requests.
 * 
 * @author Bookstore Team
 * @version 1.0
 */
public class SecurityWebApplicationInitializer extends AbstractSecurityWebApplicationInitializer {
    
    private static final Logger LOGGER = LoggerFactory.getLogger(SecurityWebApplicationInitializer.class);
    
    /**
     * Default constructor with logging.
     */
    public SecurityWebApplicationInitializer() {
        LOGGER.info("==================== SecurityWebApplicationInitializer INSTANCE CREATED ====================");
    }
    
    /**
     * Override to add additional configuration before security initialization.
     * 
     * @param servletContext the servlet context
     * @throws ServletException if an error occurs during initialization
     */
    @Override
    protected void beforeSpringSecurityFilterChain(ServletContext servletContext) {
        LOGGER.info("Initializing Spring Security for application");
        super.beforeSpringSecurityFilterChain(servletContext);
        LOGGER.info("Spring Security filter chain will be registered");
    }
    
    /**
     * Override to add additional configuration after security initialization.
     * 
     * @param servletContext the servlet context
     */
    @Override
    protected void afterSpringSecurityFilterChain(ServletContext servletContext) {
        LOGGER.info("Spring Security filter chain registered successfully");
        super.afterSpringSecurityFilterChain(servletContext);
    }
}