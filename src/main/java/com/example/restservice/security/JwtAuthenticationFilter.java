package com.example.restservice.security;

import com.example.restservice.util.JWTUtil;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.lang.NonNull;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Slf4j
@Component
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private static final String AUTHORIZATION_HEADER = "Authorization";
    private static final String BEARER_PREFIX = "Bearer ";
    private static final int BEARER_PREFIX_LENGTH = 7;

    private final JWTUtil jwtUtil;
    private final UserDetailsService userDetailsService;

    @Override
    protected void doFilterInternal(
            @NonNull HttpServletRequest request,
            @NonNull HttpServletResponse response,
            @NonNull FilterChain filterChain
    ) throws ServletException, IOException {
        
        try {
            extractAndAuthenticateToken(request);
        } catch (Exception ex) {
            log.error("Authentication error for request URI: {}", request.getRequestURI(), ex);
            SecurityContextHolder.clearContext();
        }

        filterChain.doFilter(request, response);
    }

    private void extractAndAuthenticateToken(HttpServletRequest request) {
        String jwt = extractJwtFromRequest(request);
        
        if (jwt == null) {
            return;
        }

        if (!jwtUtil.validateToken(jwt)) {
            log.warn("Invalid JWT token for request: {}", request.getRequestURI());
            return;
        }

        authenticateUser(jwt, request);
    }

    private void authenticateUser(String jwt, HttpServletRequest request) {
        String email = jwtUtil.extractEmail(jwt);
        
        if (email != null && SecurityContextHolder.getContext().getAuthentication() == null) {
            UserDetails userDetails = userDetailsService.loadUserByUsername(email);
            
            UsernamePasswordAuthenticationToken authentication = 
                new UsernamePasswordAuthenticationToken(
                    userDetails, 
                    null, 
                    userDetails.getAuthorities()
                );
            
            authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
            SecurityContextHolder.getContext().setAuthentication(authentication);
            
            log.debug("User '{}' authenticated successfully", email);
        }
    }

    private String extractJwtFromRequest(HttpServletRequest request) {
        String bearerToken = request.getHeader(AUTHORIZATION_HEADER);
        
        if (bearerToken != null && bearerToken.startsWith(BEARER_PREFIX)) {
            return bearerToken.substring(BEARER_PREFIX_LENGTH);
        }
        
        return null;
    }
}