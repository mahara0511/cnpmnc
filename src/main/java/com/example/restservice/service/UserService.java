package com.example.restservice.service;

import com.example.restservice.repository.UserRepository;
import com.example.restservice.entity.User;
import org.springframework.stereotype.Service;
import com.example.restservice.common.enums.AuthProvider;
import com.example.restservice.service.AuthService;

@Service
public class UserService {
    private final UserRepository userRepository;
    private final AuthService authService;

    public UserService(UserRepository userRepository, AuthService authService) {
        this.userRepository = userRepository;
        this.authService = authService;
    }

    public boolean existsByEmail(String email) {
        return userRepository.existsByEmail(email);
    }

    public User findByEmail(String email) {
        return userRepository.findByEmail(email).orElse(null);
    }

    public User save(String email, String password, AuthProvider provider) {
        
        return userRepository.save(new User(null, email, authService.hashPassword(password), provider));
    }

}