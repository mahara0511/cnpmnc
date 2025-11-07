package com.example.restservice.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserMeResponse {
    private Long id;
    private String name;
    private String email;
    private String role;
}
