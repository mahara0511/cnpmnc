package com.example.restservice.dto;

import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PostDto {
    private String profile;
    private String desc;
    private int exp;
    private String[] techs; 
}