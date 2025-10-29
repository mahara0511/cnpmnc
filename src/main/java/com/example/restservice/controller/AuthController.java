package com.example.restservice.controller;

import com.example.restservice.util.JWTUtil;
import com.example.restservice.repository.UserRepository;
import com.example.restservice.service.AuthService;
import com.example.restservice.service.UserService;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import java.sql.Ref;

import org.springframework.http.ResponseEntity;

import com.example.restservice.entity.RefreshToken;
import com.example.restservice.entity.User;
import com.example.restservice.common.enums.AuthProvider;
import com.example.restservice.dto.auth.*;


@RestController
@RequestMapping("/auth")
public class AuthController {
    private final UserService userService;
    private final AuthService authService;
    private final GoogleOAuth2Service googleAuthService;

    public AuthController(UserService userService, AuthService authService, GoogleOAuth2Service googleAuthService) {
        this.userService = userService;
        this.authService = authService;
        this.googleAuthService = googleAuthService;
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
        User user = userService.findByEmail(req.getEmail())
            .orElseThrow(() -> new RuntimeException("User not found"));
        if (!authService.verifyPassword(req.getPassword(), user.getPassword())) {
            return ResponseEntity.badRequest().body("Invalid credentials");
        }
        String token = authService.generateToken(user.getEmail());
        RefreshToken refreshToken = authService.createRefreshToken(user);
        return ResponseEntity.ok(AuthResponseDto.builder().accessToken(token).refreshToken(refreshToken.getToken()).build());
    }
    
    @GetMapping("/callback/google")
    public ResponseEntity<?> googleCallback(@RequestParam("code") String code) {
        try {
            // Service xử lý code -> access_token -> user info -> lưu DB -> sinh JWT
            AuthResponseDto response = googleOAuth2Service.handleGoogleCallback(code);
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
