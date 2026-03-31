package com.bookstore.web.security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;

/**
 * JWT authentication filter for processing JWT tokens in HTTP requests.
 * This filter intercepts requests, validates JWT tokens, and sets authentication
 * in the security context. Requests without valid JWT tokens are rejected
 * with 401 Unauthorized status.
 * 
 * @author Bookstore Team
 * @version 1.0
 */
@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {
    
    private static final Logger LOGGER = LoggerFactory.getLogger(JwtAuthenticationFilter.class);
    
    private static final String AUTHORIZATION_HEADER = "Authorization";
    private static final String BEARER_PREFIX = "Bearer ";
    
    private static final List<String> PUBLIC_PATHS = Arrays.asList(
            "/api/auth/login",
            "/api/auth/register",
            "/swagger-ui/",
            "/v3/api-docs/",
            "/swagger-resources/",
            "/webjars/"
    );
    
    @Autowired
    private JwtUtils jwtUtils;
    
    @Autowired
    private UserDetailsService userDetailsService;
    
    /**
     * Default constructor with logging.
     */
    public JwtAuthenticationFilter() {
        LOGGER.info("==================== JwtAuthenticationFilter INSTANCE CREATED ====================");
    }
    
    /**
     * Override to ensure filter processes all requests.
     * 
     * @param request the HTTP request
     * @return false to always process the request
     * @throws ServletException if an error occurs
     */
    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) throws ServletException {
        // Always return false to process all requests
        // The filter will decide which endpoints to skip in doFilterInternal
        return false;
    }
    
    /**
     * Process the JWT token from the request and set authentication if valid.
     * For protected endpoints, requests without a valid JWT token are rejected.
     * 
     * @param request the HTTP request
     * @param response the HTTP response
     * @param filterChain the filter chain
     * @throws ServletException if a servlet error occurs
     * @throws IOException if an I/O error occurs
     */
    @Override
    protected void doFilterInternal(final HttpServletRequest request,
                                    final HttpServletResponse response,
                                    final FilterChain filterChain)
            throws ServletException, IOException {
        
        LOGGER.debug("JwtAuthenticationFilter processing request: {} {}", 
                    request.getMethod(), request.getRequestURI());
        
        String path = request.getRequestURI();
        
        // Skip authentication for public endpoints
        if (isPublicEndpoint(path)) {
            LOGGER.debug("Public endpoint, skipping authentication: {}", path);
            filterChain.doFilter(request, response);
            return;
        }
        
        LOGGER.debug("Protected endpoint: {}", path);
        
        try {
            String jwt = parseJwt(request);
            
            // For protected endpoints, token is required
            if (jwt == null) {
                LOGGER.warn("No JWT token found for protected endpoint: {}", path);
                sendUnauthorizedResponse(response, "Authentication required. Please provide a valid JWT token.");
                return;
            }
            
            // Validate token
            if (!jwtUtils.validateToken(jwt)) {
                LOGGER.warn("Invalid JWT token for request: {}", path);
                sendUnauthorizedResponse(response, "Invalid or expired JWT token. Please authenticate again.");
                return;
            }
            
            // Extract username and load user details
            String username = jwtUtils.getUsernameFromToken(jwt);
            UserDetails userDetails = userDetailsService.loadUserByUsername(username);
            
            // Create authentication token
            UsernamePasswordAuthenticationToken authentication =
                    new UsernamePasswordAuthenticationToken(
                            userDetails, null,
                            userDetails.getAuthorities());
            authentication.setDetails(
                    new WebAuthenticationDetailsSource().buildDetails(request));
            
            // Set authentication in security context
            SecurityContextHolder.getContext().setAuthentication(authentication);
            LOGGER.debug("User authenticated successfully: {}", username);
            
            // Continue with the filter chain
            filterChain.doFilter(request, response);
            
        } catch (Exception e) {
            LOGGER.error("Authentication error for request {}: {}", path, e.getMessage(), e);
            sendUnauthorizedResponse(response, "Authentication failed: " + e.getMessage());
        }
    }
    
    /**
     * Check if the endpoint is public (no authentication required).
     * 
     * @param path the request path
     * @return true if public endpoint, false otherwise
     */
    private boolean isPublicEndpoint(String path) {
        return PUBLIC_PATHS.stream().anyMatch(path::startsWith);
    }
    
    /**
     * Extract JWT token from the Authorization header.
     * 
     * @param request the HTTP request
     * @return the JWT token or null if not present
     */
    private String parseJwt(final HttpServletRequest request) {
        String headerAuth = request.getHeader(AUTHORIZATION_HEADER);
        
        if (StringUtils.hasText(headerAuth) && headerAuth.startsWith(BEARER_PREFIX)) {
            return headerAuth.substring(BEARER_PREFIX.length());
        }
        
        return null;
    }
    
    /**
     * Send unauthorized response with error message.
     * 
     * @param response the HTTP response
     * @param message the error message
     * @throws IOException if an I/O error occurs
     */
    private void sendUnauthorizedResponse(HttpServletResponse response, String message) 
            throws IOException {
        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        response.getWriter().write(String.format(
            "{\"status\":401,\"message\":\"%s\",\"errorCode\":\"UNAUTHORIZED\"}", 
            message));
    }
}