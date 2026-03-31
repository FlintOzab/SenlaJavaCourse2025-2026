package com.bookstore.web.controller;

import bookstore.dao.jpa.JpaUserDAOInterface;
import bookstore.model.User;
import bookstore.model.UserRole;
import com.bookstore.web.dto.ErrorResponse;
import com.bookstore.web.dto.LoginRequest;
import com.bookstore.web.dto.LoginResponse;
import com.bookstore.web.security.JwtUtils;
import com.bookstore.web.security.UserDetailsServiceImpl;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * REST controller for authentication.
 * 
 * @author Bookstore Team
 * @version 1.0
 */
@RestController
@RequestMapping("/api/auth")
@Tag(name = "Authentication", description = "Authentication endpoints")
public class AuthController {
    
    /** Logger instance. */
    private static final Logger LOGGER = LoggerFactory.getLogger(
            AuthController.class);
    
    /** Authentication manager. */
    @Autowired
    private AuthenticationManager authenticationManager;
    
    /** JWT utility. */
    @Autowired
    private JwtUtils jwtUtils;
    
    /** User details service. */
    @Autowired
    private UserDetailsServiceImpl userDetailsService;
    
    /** User DAO. */
    @Autowired
    private JpaUserDAOInterface userDAO;
    
    /** Password encoder. */
    @Autowired
    private PasswordEncoder passwordEncoder;
    
    /**
     * Authenticates user and returns JWT token.
     * 
     * @param loginRequest the login request
     * @return response with JWT token
     */
    @PostMapping("/login")
    @Operation(summary = "Authenticate user", description = "Login and get JWT token")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Login successful"),
        @ApiResponse(responseCode = "401", description = "Invalid credentials")
    })
    public ResponseEntity<?> authenticateUser(
            @Valid @RequestBody final LoginRequest loginRequest) {
        LOGGER.debug("Login attempt for user: {}", loginRequest.getUsername());
        
        try {
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            loginRequest.getUsername(),
                            loginRequest.getPassword()));
            
            SecurityContextHolder.getContext().setAuthentication(authentication);
            UserDetails userDetails = userDetailsService.loadUserByUsername(
                    loginRequest.getUsername());
            String jwt = jwtUtils.generateToken(userDetails);
            
            User user = userDAO.findByUsername(loginRequest.getUsername()).get();
            
            LoginResponse response = new LoginResponse(jwt,
                    userDetails.getUsername(),
                    user.getRole().name());
            
            LOGGER.info("User logged in successfully: {}",
                    loginRequest.getUsername());
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            LOGGER.error("Login failed for user: {}",
                    loginRequest.getUsername(), e);
            ErrorResponse error = new ErrorResponse(
                    HttpStatus.UNAUTHORIZED.value(),
                    "Invalid username or password",
                    "/api/auth/login");
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(error);
        }
    }
    
    /**
     * Registers a new user.
     * 
     * @param loginRequest the registration request
     * @return response with created user info
     */
    @PostMapping("/register")
    @Operation(summary = "Register new user", description = "Create a new user account")
    @ApiResponses({
        @ApiResponse(responseCode = "201", description = "User created"),
        @ApiResponse(responseCode = "400", description = "Invalid input"),
        @ApiResponse(responseCode = "409", description = "Username already exists")
    })
    public ResponseEntity<?> registerUser(
            @Valid @RequestBody final LoginRequest loginRequest) {
        LOGGER.debug("Registration attempt for user: {}",
                loginRequest.getUsername());
        
        if (userDAO.existsByUsername(loginRequest.getUsername())) {
            ErrorResponse error = new ErrorResponse(
                    HttpStatus.CONFLICT.value(),
                    "Username already exists",
                    "/api/auth/register");
            return ResponseEntity.status(HttpStatus.CONFLICT).body(error);
        }
        
        User user = new User();
        user.setUsername(loginRequest.getUsername());
        //user.setPassword(passwordEncoder.encode(loginRequest.getPassword()));
        user.setPassword(loginRequest.getPassword());
        user.setRole(UserRole.USER);
        user.setEnabled(true);
        
        userDAO.save(user);
        
        LOGGER.info("User registered successfully: {}",
                loginRequest.getUsername());
        
        LoginResponse response = new LoginResponse(null,
                user.getUsername(),
                user.getRole().name());
        
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }
}