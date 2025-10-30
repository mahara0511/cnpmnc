package com.example.restservice.service;

import com.example.restservice.entity.RefreshToken;
import com.example.restservice.entity.User;
import com.example.restservice.repository.RefreshTokenRepository;
import com.example.restservice.repository.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.beans.factory.annotation.Value;
import java.time.Instant;
import java.util.UUID;
import java.util.Optional;
@Service
public class RefreshTokenService {

    @Value("${jwt.refreshExpirationMs}")
    private long refreshTokenDurationMs; // 7 ngày

    private RefreshTokenRepository refreshTokenRepo;
    private UserRepository userRepo;

    public RefreshTokenService(RefreshTokenRepository refreshTokenRepo, UserRepository userRepo) {
        this.refreshTokenRepo = refreshTokenRepo;
        this.userRepo = userRepo;
    }

    public RefreshToken createRefreshToken(User user) {
        RefreshToken token = new RefreshToken();
        token.setUser(user);
        token.setExpiryDate(Instant.now().plusMillis(refreshTokenDurationMs));
        token.setToken(UUID.randomUUID().toString());
        return refreshTokenRepo.save(token);
    }

    public RefreshToken verifyExpiration(RefreshToken token) {
        if (token.getExpiryDate().isBefore(Instant.now())) {
            refreshTokenRepo.delete(token);
            throw new RuntimeException("Refresh token expired");
        }
        return token;
    }
}
