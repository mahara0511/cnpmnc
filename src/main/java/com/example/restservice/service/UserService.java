package com.example.restservice.service;

import com.example.restservice.repository.UserRepository;
import com.example.restservice.entity.User;
import org.springframework.stereotype.Service;
import com.example.restservice.common.enums.AuthProvider;
import com.example.restservice.service.AuthService;
import com.example.restservice.util.JWTUtil;

import java.util.Optional;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
@Service
public class UserService {
    private final UserRepository userRepository;
    private final JWTUtil jwtUtil;

    public UserService(UserRepository userRepository, JWTUtil jwtUtil) {
        this.userRepository = userRepository;
        this.jwtUtil = jwtUtil;
    }

    private String hashPassword(String rawPassword) {
        return new BCryptPasswordEncoder().encode(rawPassword);
    }

    public boolean existsByEmail(String email) {
        return userRepository.existsByEmail(email);
    }

    public Optional<User> findByEmail(String email) {
        return userRepository.findByEmail(email);
    }

    public User save(String email, String password, AuthProvider provider) {
        User user = new User();
        user.setEmail(email);
        user.setPassword(this.hashPassword(password));
        return userRepository.save(user);
    }

    public boolean existsByJwt(String jwt) {
        return jwtUtil.validateToken(jwt);
    }

}

