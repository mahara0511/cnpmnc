package com.example.restservice.entity;

import com.example.restservice.common.enums.AuthProvider;
import jakarta.persistence.*;
import lombok.*;
import java.util.UUID;
import java.time.Instant;

@Entity
@Table(name = "users")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private UUID id;

    @Column(unique = true, nullable = false)
    private String email;

    private String name;

    private String imageLink;
    private String password;

    @Enumerated(EnumType.STRING)
    private AuthProvider provider;

    // Google OAuth tokens
    private String googleAccessToken;
    private String googleRefreshToken;
    private Instant googleTokenExpiry;
}