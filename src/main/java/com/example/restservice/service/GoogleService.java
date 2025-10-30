package com.example.restservice.service;

import com.example.restservice.entity.User;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import com.example.restservice.service.UserService;
import java.util.Map;
import com.example.restservice.repository.UserRepository;
import org.springframework.beans.factory.annotation.Value;
import java.time.Instant;
import java.util.Optional;
@Service
class GoogleService {
    private final UserService userService;
    private final UserRepository userRepo;
    @Value("${google.client.id}")
    private String clientId;
    @Value("${google.client.secret}")
    private String clientSecret;
    public GoogleService(UserService userService, UserRepository userRepo) {
        this.userService = userService;
        this.userRepo = userRepo;
    }

    public String refreshGoogleAccessToken(User user) {
        if (user.getGoogleRefreshToken() == null) {
            throw new RuntimeException("No Google refresh token available");
        }

        String tokenEndpoint = "https://oauth2.googleapis.com/token";
        RestTemplate restTemplate = new RestTemplate();

        Map<String, String> params = Map.of(
            "client_id", clientId,
            "client_secret", clientSecret,
            "refresh_token", user.getGoogleRefreshToken(),
            "grant_type", "refresh_token"
        );

        ResponseEntity<Map> response = restTemplate.postForEntity(tokenEndpoint, params, Map.class);
        Map<String, Object> tokenResponse = response.getBody();

        if (tokenResponse == null || !tokenResponse.containsKey("access_token")) {
            throw new RuntimeException("Failed to refresh access token from Google");
        }

        String newAccessToken = (String) tokenResponse.get("access_token");
        Integer expiresIn = (Integer) tokenResponse.get("expires_in"); // thường 3600 giây

        // Cập nhật DB
        user.setGoogleAccessToken(newAccessToken);
        user.setGoogleTokenExpiry(Instant.now().plusSeconds(expiresIn));
        userRepo.save(user);

        return newAccessToken;
    }

    private String getValidAccessToken(User user) {
        if (user.getGoogleTokenExpiry() == null || Instant.now().isAfter(user.getGoogleTokenExpiry())) {
            return refreshGoogleAccessToken(user);
        }
        return user.getGoogleAccessToken();
    }

    public Map<String, Object> getUserDriveFiles(String email) {
        User user = userService.findByEmail(email)
            .orElseThrow(() -> new RuntimeException("User not found"));

        if (user.getGoogleAccessToken() == null) {
            throw new RuntimeException("User not found or Google token missing");
        }
        String accessToken = getValidAccessToken(user); // luôn lấy token mới nếu hết hạn
        String driveEndpoint = "https://www.googleapis.com/drive/v3/files";

        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(accessToken);

        HttpEntity<Void> entity = new HttpEntity<>(headers);
        RestTemplate restTemplate = new RestTemplate();

        ResponseEntity<Map> response = restTemplate.exchange(
                driveEndpoint, HttpMethod.GET, entity, Map.class);

        return response.getBody();
    }
}