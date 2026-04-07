package com.sanju.measurement_service.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.nio.charset.StandardCharsets;
import java.security.Key;
import java.util.Date;

@Service
public class JwtService {
    
    @Value("${application.security.jwt.secret-key}")
    private String jwtSecretKey;
    
    private Key getSigningKey() {
        byte[] keyBytes = jwtSecretKey.getBytes(StandardCharsets.UTF_8);
        return Keys.hmacShaKeyFor(keyBytes);
    }
    
    /**
     * Extract userId from JWT token
     */
    public Long extractUserId(String token) {
        Claims claims = getClaims(token);

        // Auth service contract: subject carries the user id.
        Object userIdObj = claims.getSubject();
        if (userIdObj == null || (userIdObj instanceof String value && value.isBlank())) {
            userIdObj = claims.get("id");
        }
        if (userIdObj == null) {
            userIdObj = claims.get("userId");
        }
        if (userIdObj instanceof Number number) {
            return number.longValue();
        }
        if (userIdObj instanceof String value && !value.isBlank()) {
            try {
                return Long.parseLong(value);
            } catch (NumberFormatException ignored) {
                return null;
            }
        }
        return null;
    }
    
    /**
     * Validate JWT token (signature and expiration)
     */
    public boolean validateToken(String token) {
        try {
            Claims claims = Jwts.parserBuilder()
                .setSigningKey(getSigningKey())
                .build()
                .parseClaimsJws(token)
                .getBody();
            
            // Check expiration
            Date expiration = claims.getExpiration();
            return expiration.after(new Date());
        } catch (JwtException | IllegalArgumentException e) {
            return false;
        }
    }
    
    /**
     * Get claims from JWT token
     */
    private Claims getClaims(String token) {
        return Jwts.parserBuilder()
            .setSigningKey(getSigningKey())
            .build()
            .parseClaimsJws(token)
            .getBody();
    }
}
