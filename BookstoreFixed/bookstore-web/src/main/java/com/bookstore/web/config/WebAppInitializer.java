package com.bookstore.web.config;

import bookstore.app.AppConfig;
import jakarta.servlet.MultipartConfigElement;
import jakarta.servlet.ServletContext;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRegistration;
import org.springframework.web.WebApplicationInitializer;
import org.springframework.web.context.ContextLoaderListener;
import org.springframework.web.context.support.AnnotationConfigWebApplicationContext;
import org.springframework.web.servlet.DispatcherServlet;

/**
 * Web application initializer for the Bookstore application.
 * Configures application context with all configurations.
 * 
 * @author Bookstore Team
 * @version 1.0
 */
public class WebAppInitializer implements WebApplicationInitializer {
    
    private static final String TEMP_LOCATION = "/tmp";
    private static final long MAX_FILE_SIZE = 10 * 1024 * 1024;
    private static final long MAX_REQUEST_SIZE = 20 * 1024 * 1024;
    private static final int FILE_SIZE_THRESHOLD = 2 * 1024 * 1024;
    
    @Override
    public void onStartup(final ServletContext servletContext) throws ServletException {
        // Create a single application context with all configurations
        AnnotationConfigWebApplicationContext applicationContext = 
                new AnnotationConfigWebApplicationContext();
        
        // Register ALL configurations in the same context
        applicationContext.register(AppConfig.class);
        applicationContext.register(WebConfig.class);
        applicationContext.register(SecurityConfig.class);
        
        // Add listener to manage context lifecycle
        servletContext.addListener(new ContextLoaderListener(applicationContext));
        
        // Register DispatcherServlet with the same context
        ServletRegistration.Dynamic dispatcher = servletContext.addServlet(
            "dispatcher", new DispatcherServlet(applicationContext));
        dispatcher.setLoadOnStartup(1);
        dispatcher.addMapping("/");
        
        // Configure multipart support
        MultipartConfigElement multipartConfig = new MultipartConfigElement(
            TEMP_LOCATION, MAX_FILE_SIZE, MAX_REQUEST_SIZE, FILE_SIZE_THRESHOLD);
        dispatcher.setMultipartConfig(multipartConfig);
    }
}