package com.example.restservice.dto.auth;

import lombok.*;

@Data
@NoArgsConstructor  
@AllArgsConstructor
public class GoogleAuthRequestDto {
    private String token;
}