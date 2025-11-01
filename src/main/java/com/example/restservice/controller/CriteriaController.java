package com.example.restservice.controller;

import com.example.restservice.dto.CreateCriteriaDTO;
import com.example.restservice.response.CriteriaResponseDTO;
import com.example.restservice.security.CustomUserDetailsService;
import com.example.restservice.service.CriteriaService;
import com.example.restservice.service.UserService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@RestController
@RequestMapping("/criteria")
public class CriteriaController {
    private final CriteriaService criteriaService;
    private final UserService userService;

    public CriteriaController(CriteriaService criteriaService, UserService userService) {
        this.criteriaService = criteriaService;
        this.userService = userService;
    }

    @PostMapping("")
    public ResponseEntity<?> create(@RequestBody CreateCriteriaDTO req) {
        try {
            CriteriaResponseDTO res = criteriaService.create(req);
            return ResponseEntity.ok(res);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Refresh failed: " + e.getMessage());
        }
    }
}
