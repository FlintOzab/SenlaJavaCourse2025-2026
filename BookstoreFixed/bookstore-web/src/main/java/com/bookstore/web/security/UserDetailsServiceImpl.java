package com.bookstore.web.security;

import bookstore.dao.jpa.JpaUserDAOInterface;
import bookstore.model.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

/**
 * Implementation of UserDetailsService for loading user-specific data.
 * 
 * @author Bookstore Team
 * @version 1.0
 */
@Service
public class UserDetailsServiceImpl implements UserDetailsService {
    
    private static final Logger LOGGER = LoggerFactory.getLogger(UserDetailsServiceImpl.class);
    
    @Autowired
    private JpaUserDAOInterface userDAO;
    
    /**
     * Load user by username for Spring Security.
     * 
     * @param username the username to load
     * @return the UserDetails object
     * @throws UsernameNotFoundException if user not found
     */
    @Override
    public UserDetails loadUserByUsername(final String username)
            throws UsernameNotFoundException {
        User user = userDAO.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException(
                        "User not found with username: " + username));
        
        List<GrantedAuthority> authorities = new ArrayList<>();
        authorities.add(new SimpleGrantedAuthority("ROLE_" + user.getRole().name()));
        
        LOGGER.debug("Loading user: {} with role: {}", username, user.getRole());
        
        return new org.springframework.security.core.userdetails.User(
                user.getUsername(),
                user.getPassword(),
                user.isEnabled(),
                true,
                true,
                true,
                authorities);
    }
}