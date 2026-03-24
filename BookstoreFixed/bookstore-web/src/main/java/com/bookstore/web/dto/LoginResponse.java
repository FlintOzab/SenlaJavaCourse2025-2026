package com.bookstore.web.dto;

/**
 * DTO for login responses containing JWT token.
 * 
 * @author Bookstore Team
 * @version 1.0
 */
public class LoginResponse {
    
    private String token;
    private String type;
    private String username;
    private String role;
    
    /**
     * Default constructor.
     */
    public LoginResponse() {
    }
    
    /**
     * Constructs a new login response.
     * 
     * @param tokenValue the JWT token
     * @param usernameValue the username
     * @param roleValue the user role
     */
    public LoginResponse(final String tokenValue, final String usernameValue,
                         final String roleValue) {
        this.token = tokenValue;
        this.type = "Bearer";
        this.username = usernameValue;
        this.role = roleValue;
    }
    
    /**
     * Gets the token.
     * 
     * @return the token
     */
    public String getToken() {
        return token;
    }
    
    /**
     * Sets the token.
     * 
     * @param tokenValue the new token
     */
    public void setToken(final String tokenValue) {
        this.token = tokenValue;
    }
    
    /**
     * Gets the token type.
     * 
     * @return the token type
     */
    public String getType() {
        return type;
    }
    
    /**
     * Sets the token type.
     * 
     * @param typeValue the new token type
     */
    public void setType(final String typeValue) {
        this.type = typeValue;
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
     * Gets the user role.
     * 
     * @return the role
     */
    public String getRole() {
        return role;
    }
    
    /**
     * Sets the user role.
     * 
     * @param roleValue the new role
     */
    public void setRole(final String roleValue) {
        this.role = roleValue;
    }
}