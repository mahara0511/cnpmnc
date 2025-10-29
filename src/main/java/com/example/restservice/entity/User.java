package java.com.example.restservice.entity;

import com.example.restservice.common.enums.AuthProvider;
import jakarta.persistence.*;
import lombok.*;
import java.util.UUID;

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

    private String imageLink;
    private String password;

    @Enumerated(EnumType.STRING)
    private AuthProvider authProvider;

    // Google OAuth tokens
    private String googleAccessToken;
    private String googleRefreshToken;
    private Instant googleTokenExpiry;
}