package com.example.restservice.controller;

import com.example.restservice.service.UserService;
import org.springframework.web.bind.annotation.*;
import io.swagger.v3.oas.annotations.tags.Tag;

@RestController
@Tag(name = "User Management", description = "Endpoints for user management")
@RequestMapping("/users")
class UserController {
    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping("/exists")
    public boolean checkUserExists(@RequestParam String jwtString) {
        return userService.existsByJwt(jwtString);
    }
}