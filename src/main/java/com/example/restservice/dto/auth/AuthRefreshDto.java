package com.example.restservice.dto.auth;

import lombok.*;

@Data
@AllArgsConstructor 
@NoArgsConstructor
public class AuthRefreshDto {
    private String refreshToken;
}