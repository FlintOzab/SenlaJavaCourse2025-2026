package com.bookstore.web.config;

import jakarta.servlet.MultipartConfigElement;
import jakarta.servlet.ServletContext;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRegistration;
import org.springframework.web.WebApplicationInitializer;
import org.springframework.web.context.ContextLoaderListener;
import org.springframework.web.context.support.AnnotationConfigWebApplicationContext;
import org.springframework.web.servlet.DispatcherServlet;

import bookstore.app.AppConfig;

/**
 * Web application initializer for Servlet 3.0+ containers
 */
public class WebAppInitializer implements WebApplicationInitializer {
    
    private static final String TEMP_LOCATION = "/tmp";
    private static final long MAX_FILE_SIZE = 10 * 1024 * 1024; // 10 MB
    private static final long MAX_REQUEST_SIZE = 20 * 1024 * 1024; // 20 MB
    private static final int FILE_SIZE_THRESHOLD = 2 * 1024 * 1024; // 2 MB
    
    @Override
    public void onStartup(ServletContext servletContext) throws ServletException {
        // Create root application context
        AnnotationConfigWebApplicationContext rootContext = new AnnotationConfigWebApplicationContext();
        rootContext.register(AppConfig.class);
        
        // Manage the lifecycle of the root application context
        servletContext.addListener(new ContextLoaderListener(rootContext));
        
        // Create dispatcher servlet context
        AnnotationConfigWebApplicationContext webContext = new AnnotationConfigWebApplicationContext();
        webContext.register(WebConfig.class);
        webContext.setParent(rootContext);
        // Register and map the dispatcher servlet
        ServletRegistration.Dynamic dispatcher = servletContext.addServlet(
            "dispatcher", new DispatcherServlet(webContext));
        dispatcher.setLoadOnStartup(1);
        dispatcher.addMapping("/");
        
        // Configure multipart for file uploads
        MultipartConfigElement multipartConfig = new MultipartConfigElement(
            TEMP_LOCATION, MAX_FILE_SIZE, MAX_REQUEST_SIZE, FILE_SIZE_THRESHOLD);
        dispatcher.setMultipartConfig(multipartConfig);
    }
}