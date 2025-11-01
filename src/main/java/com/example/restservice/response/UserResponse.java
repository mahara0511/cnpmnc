package com.example.restservice.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;
@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserResponse {
   private  String name;
    private String email;
   private  Long id;
}
