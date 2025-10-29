package com.example.restservice.dto.auth;

import lombok.*;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class AuthResponseDto {
    private String accessToken;
    private String refreshToken;

}