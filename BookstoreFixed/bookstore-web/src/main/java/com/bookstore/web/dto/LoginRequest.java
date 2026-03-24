package com.bookstore.web.dto;

import jakarta.validation.constraints.NotBlank;

/**
 * DTO for login requests.
 * 
 * @author Bookstore Team
 * @version 1.0
 */
public class LoginRequest {
    
    @NotBlank(message = "Username is required")
    private String username;
    
    @NotBlank(message = "Password is required")
    private String password;
    
    /**
     * Default constructor.
     */
    public LoginRequest() {
    }
    
    /**
     * Constructs a new login request.
     * 
     * @param usernameValue the username
     * @param passwordValue the password
     */
    public LoginRequest(final String usernameValue, final String passwordValue) {
        this.username = usernameValue;
        this.password = passwordValue;
    }
    
    /**
     * Gets the username.
     * 
     * @return the username
     */
    public String getUsername() {
        return username;
    }
    
    /**
     * Sets the username.
     * 
     * @param usernameValue the new username
     */
    public void setUsername(final String usernameValue) {
        this.username = usernameValue;
    }
    
    /**
     * Gets the password.
     * 
     * @return the password
     */
    public String getPassword() {
        return password;
    }
    
    /**
     * Sets the password.
     * 
     * @param passwordValue the new password
     */
    public void setPassword(final String passwordValue) {
        this.password = passwordValue;
    }
}