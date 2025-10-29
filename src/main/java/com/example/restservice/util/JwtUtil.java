package com.example.restservice.util;

import io.jsonwebtoken.*;
import org.springframework.stereotype.Component;
import java.util.Date;
import org.springframework.beans.factory.annotation.Value;

@Component
public class JWTUtil {
    
    private final String secret;
    private final long jwtExpirationInMs;

    public JWTUtil(
        @Value("${jwt.secret}") String secret, 
        @Value("${jwt.expirationMs}") long jwtExpirationInMs
    ) {
        this.secret = secret;
        this.jwtExpirationInMs = jwtExpirationInMs;
    }

    public String generationToken(String email) {
        Date now = new Date();
        Date expiryDate = new Date(now.getTime() + jwtExpirationInMs);

        return Jwts.builder()
                .setSubject(email)
                .setIssuedAt(now)
                .setExpiration(expiryDate)
                .signWith(SignatureAlgorithm.HS512, secret)
                .compact();
    }

    public String extractEmail(String token) {
        return Jwts.parser().setSigningKey(secret)
                .parseClaimsJws(token)
                .getBody()
                .getSubject();
    }
}