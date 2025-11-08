package com.example.restservice.controller;

import com.example.restservice.dto.ApiResponse;
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
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.Optional;
import com.example.restservice.entity.Supervisor;
import com.example.restservice.entity.Employee;

@RestController
@Tag(name = "Authentication", description = "Endpoints for user authentication and registration")
@RequestMapping("/auth")
public class AuthController {
    private final UserService userService;
    private final AuthService authService;
    private final RefreshTokenService refreshTokenService;
    private final GoogleOAuth2Service googleAuthService;

    public AuthController(UserService userService, AuthService authService,
                          RefreshTokenService refreshTokenService, GoogleOAuth2Service googleAuthService) {
        this.userService = userService;
        this.authService = authService;
        this.refreshTokenService = refreshTokenService;
        this.googleAuthService = googleAuthService;
    }

    // 🔹 Đăng ký tài khoản
//    @PostMapping("/register")
//    public ResponseEntity<ApiResponse<?>> register(@RequestBody AuthRequestDto req) {
//        if (userService.existsByEmail(req.getEmail())) {
//            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
//                    .body(ApiResponse.error(400, "Email already exists"));
//        }
//
//        userService.save(req.getEmail(), req.getPassword(), AuthProvider.LOCAL);
//        return ResponseEntity.ok(ApiResponse.success(200, "Registered successfully", null));
//    }

    // 🔹 Đăng nhập
    @PostMapping("/login")
    public ResponseEntity<ApiResponse<?>> login(@RequestBody AuthRequestDto req) {
        Optional<User> optionalUser = userService.findByEmail(req.getEmail());

        if (optionalUser.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(ApiResponse.error(404, "User not found"));
        }

        User user = optionalUser.get();

        if (!authService.verifyPassword(req.getPassword(), user.getPassword())) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(ApiResponse.error(401, "Invalid credentials"));
        }


        String role;
        if (user instanceof Supervisor) {
            role = "SUPERVISOR";
        } else if (user instanceof Employee) {
            role = "EMPLOYEE";
        } else {
            role = "USER";
        }


        String token = authService.generateToken(user.getEmail(), user.getId(), role);
        RefreshToken refreshToken = refreshTokenService.createRefreshToken(user);

        AuthResponseDto authResponse = AuthResponseDto.builder()
                .accessToken(token)
                .refreshToken(refreshToken.getToken())
                .build();

        return ResponseEntity.ok(ApiResponse.success(200, "Login successful", authResponse));
    }

    // 🔹 Google OAuth2 callback
    @GetMapping("/callback/google")
    public ResponseEntity<ApiResponse<?>> googleCallback(@RequestParam("code") String code) {
        try {
            AuthResponseDto response = googleAuthService.handleGoogleCallback(code);
            return ResponseEntity.ok(ApiResponse.success(200, "Google login successful", response));
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                    .body(ApiResponse.error(400, "Google OAuth2 failed: " + e.getMessage()));
        }
    }

    // 🔹 Làm mới token
    @PostMapping("/refresh")
    public ResponseEntity<ApiResponse<?>> refreshToken(@RequestBody AuthRefreshDto req) {
        try {
            AuthResponseDto response = authService.refreshAccessToken(req);
            return ResponseEntity.ok(ApiResponse.success(200, "Token refreshed", response));
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                    .body(ApiResponse.error(400, "Refresh failed: " + e.getMessage()));
        }
    }
}
