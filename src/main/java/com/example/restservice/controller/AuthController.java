package com.example.restservice.controller;

import com.example.restservice.util.JWTUtil;
import com.example.restservice.repository.UserRepository;
import com.example.restservice.service.AuthService;
import com.example.restservice.service.UserService;
import com.example.restservice.service.RefreshTokenService;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.sql.Ref;

import org.springframework.http.ResponseEntity;

import com.example.restservice.entity.RefreshToken;
import com.example.restservice.entity.User;
import com.example.restservice.common.enums.AuthProvider;
import com.example.restservice.dto.auth.*;
import com.example.restservice.service.GoogleOAuth2Service;
import com.example.restservice.service.RefreshTokenService;
import com.example.restservice.service.AuthService;
import java.util.Optional;
import java.util.Map;
import org.springframework.http.HttpStatus;
import io.swagger.v3.oas.annotations.tags.Tag;

@RestController
@Tag(name = "Authentication", description = "Endpoints for user authentication and registration")
@RequestMapping("/auth")
public class AuthController {
    private final UserService userService;
    private final AuthService authService;
    private final RefreshTokenService refreshTokenService;
    private final GoogleOAuth2Service googleAuthService;

    public AuthController(UserService userService, AuthService authService, RefreshTokenService refreshTokenService, GoogleOAuth2Service googleAuthService) {
        this.userService = userService;
        this.authService = authService;
        this.googleAuthService = googleAuthService;
        this.refreshTokenService = refreshTokenService;
    }
    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody AuthRequestDto req) {
        if (userService.existsByEmail(req.getEmail())) {
            return ResponseEntity.badRequest().body("Email already exists");
        }

        userService.save(req.getEmail(), req.getPassword(), AuthProvider.LOCAL);
        return ResponseEntity.ok("Registered successfully");
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody AuthRequestDto req) {
        Optional<User> optionalUser = userService.findByEmail(req.getEmail());

        if (optionalUser.isEmpty()) {
            return ResponseEntity
                .status(HttpStatus.NOT_FOUND)
                .body(Map.of("error", "User not found"));
        }

        User user = optionalUser.get();

        if (!authService.verifyPassword(req.getPassword(), user.getPassword())) {
            return ResponseEntity
                .status(HttpStatus.UNAUTHORIZED)
                .body(Map.of("error", "Invalid credentials"));
        }

        String token = authService.generateToken(user.getEmail());
        RefreshToken refreshToken = refreshTokenService.createRefreshToken(user);

        return ResponseEntity.ok(
            AuthResponseDto.builder()
                .accessToken(token)
                .refreshToken(refreshToken.getToken())
                .build()
        );
    }

    
    @GetMapping("/callback/google")
    public ResponseEntity<?> googleCallback(@RequestParam("code") String code) {
        try {
            // Service xử lý code -> access_token -> user info -> lưu DB -> sinh JWT
            AuthResponseDto response = googleAuthService.handleGoogleCallback(code);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Google OAuth2 failed: " + e.getMessage());
        }
    }

    @PostMapping("/refresh")
    public ResponseEntity<?> refreshToken(@RequestBody AuthRefreshDto req) {
        try {
            return ResponseEntity.ok(authService.refreshAccessToken(req));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Refresh failed: " + e.getMessage());
        }
    }
}
