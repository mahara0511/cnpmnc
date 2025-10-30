package com.example.restservice.entity;

import jakarta.persistence.*;
import java.time.Instant;
import java.util.UUID;
import com.example.restservice.entity.User;
import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "refresh_tokens")
public class RefreshToken {

    @Id
    @GeneratedValue()
    private UUID id;

    @Column(nullable = false, unique = true)
    private String token;

    @OneToOne
    @JoinColumn(name = "user_id", referencedColumnName = "id")
    private User user;

    @Column(nullable = false)
    private Instant expiryDate;

    // getters/setters
}
