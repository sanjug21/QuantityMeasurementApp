package com.sanju.measurement_service.security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Collections;

@Component
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {
    private final JwtService jwtService;

    @Override
    protected void doFilterInternal(
            @NonNull HttpServletRequest request,
            @NonNull HttpServletResponse response,
            @NonNull FilterChain filterChain)
            throws ServletException, IOException {
        
        final String authHeader = request.getHeader("Authorization");
        final String jwt;
        
        // Check if Authorization header exists and starts with "Bearer "
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            filterChain.doFilter(request, response);
            return;
        }

        // Extract JWT token from header (remove "Bearer " prefix)
        jwt = authHeader.substring(7);
        // Validate JWT token (signature, expiration, etc.) before extracting claims.
        if (jwtService.validateToken(jwt) && SecurityContextHolder.getContext().getAuthentication() == null) {
            Long userId = jwtService.extractUserId(jwt);
            // If userId exists and user is not already authenticated
            if (userId != null) {
                // Create authentication token
                UsernamePasswordAuthenticationToken authToken =
                    new UsernamePasswordAuthenticationToken(
                        userId,
                        null,
                        Collections.emptyList()
                    );

                authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));

                // Set authentication in SecurityContext (makes it available for this request)
                SecurityContextHolder.getContext().setAuthentication(authToken);
            }
        }

        // Continue with filter chain
        filterChain.doFilter(request, response);
    }
}
