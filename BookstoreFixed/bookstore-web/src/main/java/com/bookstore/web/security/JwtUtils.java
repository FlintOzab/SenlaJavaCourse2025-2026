package com.bookstore.web.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.SignatureException;
import io.jsonwebtoken.UnsupportedJwtException;
import io.jsonwebtoken.security.Keys;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import java.security.Key;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

/**
 * Utility class for JWT token operations.
 * Handles token generation, validation, and claim extraction.
 * 
 * @author Bookstore Team
 * @version 1.0
 */
@Component
public class JwtUtils {
    
    private static final Logger LOGGER = LoggerFactory.getLogger(JwtUtils.class);
    
    @Value("${jwt.secret:bookstoreSecretKeyForJWTTokenGeneration1234567890}")
    private String jwtSecret;
    
    @Value("${jwt.expiration:86400000}")
    private int jwtExpiration;
    
    /**
     * Extract username from JWT token.
     * 
     * @param token the JWT token
     * @return the username
     */
    public String getUsernameFromToken(final String token) {
        return getClaimFromToken(token, Claims::getSubject);
    }
    
    /**
     * Extract expiration date from JWT token.
     * 
     * @param token the JWT token
     * @return the expiration date
     */
    public Date getExpirationDateFromToken(final String token) {
        return getClaimFromToken(token, Claims::getExpiration);
    }
    
    /**
     * Extract specific claim from JWT token.
     * 
     * @param token the JWT token
     * @param claimsResolver the claims resolver function
     * @param <T> the claim type
     * @return the extracted claim
     */
    public <T> T getClaimFromToken(final String token,
                                   final Function<Claims, T> claimsResolver) {
        final Claims claims = getAllClaimsFromToken(token);
        return claimsResolver.apply(claims);
    }
    
    /**
     * Get all claims from JWT token.
     * 
     * @param token the JWT token
     * @return the claims
     */
    private Claims getAllClaimsFromToken(final String token) {
        return Jwts.parserBuilder()
                .setSigningKey(getSigningKey())
                .build()
                .parseClaimsJws(token)
                .getBody();
    }
    
    /**
     * Check if token is expired.
     * 
     * @param token the JWT token
     * @return true if expired, false otherwise
     */
    private Boolean isTokenExpired(final String token) {
        final Date expiration = getExpirationDateFromToken(token);
        return expiration.before(new Date());
    }
    
    /**
     * Generate JWT token for user.
     * 
     * @param userDetails the user details
     * @return the generated JWT token
     */
    public String generateToken(final UserDetails userDetails) {
        Map<String, Object> claims = new HashMap<>();
        claims.put("role", userDetails.getAuthorities().iterator().next().getAuthority());
        return doGenerateToken(claims, userDetails.getUsername());
    }
    
    /**
     * Generate JWT token with claims and subject.
     * 
     * @param claims the claims to include
     * @param subject the subject (username)
     * @return the generated JWT token
     */
    private String doGenerateToken(final Map<String, Object> claims,
                                   final String subject) {
        return Jwts.builder()
                .setClaims(claims)
                .setSubject(subject)
                .setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(new Date(System.currentTimeMillis() + jwtExpiration))
                .signWith(getSigningKey(), SignatureAlgorithm.HS256)
                .compact();
    }
    
    /**
     * Validate JWT token against user details.
     * 
     * @param token the JWT token
     * @param userDetails the user details
     * @return true if valid, false otherwise
     */
    public Boolean validateToken(final String token, final UserDetails userDetails) {
        final String username = getUsernameFromToken(token);
        return (username.equals(userDetails.getUsername()) && !isTokenExpired(token));
    }
    
    /**
     * Validate JWT token format and signature.
     * 
     * @param token the JWT token
     * @return true if valid, false otherwise
     */
    public boolean validateToken(final String token) {
        try {
            Jwts.parserBuilder().setSigningKey(getSigningKey()).build()
                    .parseClaimsJws(token);
            return true;
        } catch (SignatureException e) {
            LOGGER.error("Invalid JWT signature: {}", e.getMessage());
        } catch (MalformedJwtException e) {
            LOGGER.error("Invalid JWT token: {}", e.getMessage());
        } catch (ExpiredJwtException e) {
            LOGGER.error("JWT token is expired: {}", e.getMessage());
        } catch (UnsupportedJwtException e) {
            LOGGER.error("JWT token is unsupported: {}", e.getMessage());
        } catch (IllegalArgumentException e) {
            LOGGER.error("JWT claims string is empty: {}", e.getMessage());
        }
        return false;
    }
    
    /**
     * Get signing key for JWT operations.
     * 
     * @return the signing key
     */
    private Key getSigningKey() {
        return Keys.hmacShaKeyFor(jwtSecret.getBytes());
    }
}