package com.example.restservice.service;

import com.example.restservice.entity.User;
import com.example.restservice.repository.UserRepository;
import com.example.restservice.util.JWTUtil;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

// Google OAuth
import com.google.api.client.googleapis.auth.oauth2.GoogleIdToken;
import com.google.api.client.googleapis.auth.oauth2.GoogleIdTokenVerifier;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.gson.GsonFactory;

import com.example.restservice.common.enums.AuthProvider;
import com.example.restservice.dto.auth.AuthResponseDto;

import java.util.Collections;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.example.restservice.repository.RefreshTokenRepository;
import com.example.restservice.service.RefreshTokenService;
import com.example.restservice.entity.RefreshToken;

import com.example.restservice.repository.UserRepository;
import com.example.restservice.entity.User;

import com.example.restservice.dto.auth.AuthRefreshDto;
import com.example.restservice.dto.auth.AuthResponseDto;
import com.example.restservice.common.enums.AuthProvider;
import com.example.restservice.util.JWTUtil;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import com.example.restservice.service.RefreshTokenService;
import com.example.restservice.repository.RefreshTokenRepository;
import com.example.restservice.entity.RefreshToken;
import com.example.restservice.repository.UserRepository;
import com.example.restservice.entity.User;
import java.util.Optional;
import com.example.restservice.entity.Supervisor;
import com.example.restservice.entity.Employee;

@Service
public class AuthService {
    private final UserService userService;
    private final JWTUtil jwtUtil;
    private final BCryptPasswordEncoder passwordEncoder;
    private final RefreshTokenRepository refreshTokenRepo;
    private final RefreshTokenService refreshTokenService;
    private final UserRepository userRepo;
    @Value("${google.client.id}")
    private String googleClientId;

    public AuthService(UserService userService, JWTUtil jwtUtil, RefreshTokenRepository refreshTokenRepo, RefreshTokenService refreshTokenService, UserRepository userRepo) {
        this.userService = userService;
        this.jwtUtil = jwtUtil;
        this.refreshTokenRepo = refreshTokenRepo;
        this.refreshTokenService = refreshTokenService;
        this.passwordEncoder = new BCryptPasswordEncoder();
        this.userRepo = userRepo;
    }

    public String hashPassword(String rawPassword) {
        return passwordEncoder.encode(rawPassword);
    }

    public boolean verifyPassword(String rawPassword, String encodedPassword) {
        return passwordEncoder.matches(rawPassword, encodedPassword);
    }
    public String generateToken(String email, Long userId, String role) {
        return jwtUtil.generateToken(email, userId, role);
    }

    public String authenticate(String googleToken) throws Exception {
        GoogleIdTokenVerifier verifier = new GoogleIdTokenVerifier.Builder(
                new NetHttpTransport(),
                new GsonFactory()
        ).setAudience(Collections.singletonList(googleClientId))
                .build();

        GoogleIdToken idToken = verifier.verify(googleToken);

        if (idToken == null) {
            throw new RuntimeException("Invalid Google token");
        }

        String email = idToken.getPayload().getEmail();

        User user = userRepo.findByEmail(email).orElseGet(() -> {
            User newUser = new User();
            newUser.setEmail(email);
            return userRepo.save(newUser);
        });

        return jwtUtil.generateToken(user.getEmail(), user.getId(), "USER");
    }

    public AuthResponseDto refreshAccessToken(AuthRefreshDto req) {
        String requestRefreshToken = req.getRefreshToken();

        RefreshToken refreshToken = refreshTokenRepo.findByToken(requestRefreshToken)
                .orElseThrow(() -> new RuntimeException("Refresh token not found"));

        refreshTokenService.verifyExpiration(refreshToken);
        User user = refreshToken.getUser();
        String role;
        if (user instanceof Supervisor) {
            role = "SUPERVISOR";
        } else if (user instanceof Employee) {
            role = "EMPLOYEE";
        } else {
            role = "USER";
        }
        String newAccessToken = jwtUtil.generateToken(user.getEmail(), user.getId(), role);

        return AuthResponseDto.builder()
                .accessToken(newAccessToken)
                .refreshToken(requestRefreshToken)
                .build();
    }

}