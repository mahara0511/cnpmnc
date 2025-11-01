package com.example.restservice.service;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import com.example.restservice.service.GoogleService;
import io.swagger.v3.oas.annotations.tags.Tag;
@RestController
@Tag(name = "Google API", description = "Endpoints for Google API interactions")
@RequestMapping("/google")
public class GoogleController {
    private final GoogleService googleService;

    public GoogleController(GoogleService googleService) {
        this.googleService = googleService;
    }

    
    @GetMapping("/drive/files")
    public Object getDriveFiles(@RequestParam("email") String email) {
//        return googleService.getUserDriveFiles(email);
        return null;
    }
}