package com.example.restservice.service;

import com.example.restservice.common.enums.AuthProvider;
import com.example.restservice.dto.auth.AuthResponseDto;
import com.example.restservice.entity.RefreshToken;
import com.example.restservice.entity.User;
import com.example.restservice.repository.UserRepository;
import com.example.restservice.util.JWTUtil;
import com.example.restservice.service.RefreshTokenService;
import com.example.restservice.service.AuthService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.time.Instant;
import java.util.Map;

@Service
public class GoogleOAuth2Service {

    @Value("${google.client.id}")
    private String clientId;

    @Value("${google.client.secret}")
    private String clientSecret;

    @Value("${google.redirect.uri}")
    private String redirectUri;

    private final UserRepository userRepo;
    private final AuthService authService;
    private final RefreshTokenService refreshTokenService;
    private final JWTUtil jwtUtil;

    public GoogleOAuth2Service(UserRepository userRepo, AuthService authService, JWTUtil jwtUtil, RefreshTokenService refreshTokenService) {
        this.userRepo = userRepo;
        this.authService = authService;
        this.jwtUtil = jwtUtil;
        this.refreshTokenService = refreshTokenService; 
    }

    public AuthResponseDto handleGoogleCallback(String code) {
        // 1. Đổi code -> access token
        String tokenEndpoint = "https://oauth2.googleapis.com/token";
        RestTemplate restTemplate = new RestTemplate();

        Map<String, String> params = Map.of(
            "code", code,
            "client_id", clientId,
            "client_secret", clientSecret,
            "redirect_uri", redirectUri,
            "grant_type", "authorization_code"
        );

        ResponseEntity<Map> response = restTemplate.postForEntity(tokenEndpoint, params, Map.class);
        Map<String, Object> tokenResponse = response.getBody();

        if (tokenResponse == null || !tokenResponse.containsKey("access_token")) {
            throw new RuntimeException("Failed to get access token from Google");
        }

        String accessToken = (String) tokenResponse.get("access_token");
        String refreshTokenGoogle = (String) tokenResponse.get("refresh_token");
        Integer expiresIn = (Integer) tokenResponse.get("expires_in"); // thường là 3600 giây

        // 2. Lấy user info từ Google
        String userInfoEndpoint = "https://www.googleapis.com/oauth2/v2/userinfo";
        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(accessToken);

        HttpEntity<Void> entity = new HttpEntity<>(headers);
        ResponseEntity<Map> userInfoResp = restTemplate.exchange(
                userInfoEndpoint, HttpMethod.GET, entity, Map.class);

        Map<String, Object> userInfo = userInfoResp.getBody();
        String email = (String) userInfo.get("email");
        String name = (String) userInfo.get("name");
        String picture = (String) userInfo.get("picture");

        // 3. Lưu hoặc cập nhật user trong DB
        User user = userRepo.findByEmail(email).orElseGet(() -> {
            User newUser = new User();
            newUser.setEmail(email);
            return newUser;
        });

        user.setName(name);
        userRepo.save(user);

        // 4. Sinh JWT nội bộ + refresh token nội bộ
        String jwtAccess = jwtUtil.generateToken(user.getEmail(), user.getId(), "EMPLOYEE");
        RefreshToken refreshToken = refreshTokenService.createRefreshToken(user);

        // 5. Trả về response
        return AuthResponseDto.builder()
                .accessToken(jwtAccess)
                .refreshToken(refreshToken.getToken())
                .build();
    }
}
