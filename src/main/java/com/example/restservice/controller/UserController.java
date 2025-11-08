package com.example.restservice.controller;

import com.example.restservice.dto.CreateEmployeeDTO;
import com.example.restservice.dto.DashboardSummaryResponse;
import com.example.restservice.response.ApiResponse;
import com.example.restservice.response.UserMeResponse;
import com.example.restservice.response.UserResponse;
import com.example.restservice.service.AssessmentService;
import com.example.restservice.service.UserService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;
import io.swagger.v3.oas.annotations.tags.Tag;

@RestController
@Tag(name = "User Management", description = "Endpoints for user management")
@RequestMapping("/users")
class UserController {
    private final UserService userService;
    private final AssessmentService assessmentService;

    public UserController(UserService userService, AssessmentService assessmentService) {
        this.userService = userService;
        this.assessmentService = assessmentService;
    }
    @GetMapping("/exists")
    public boolean checkUserExists(@RequestParam String jwtString) {
        return userService.existsByJwt(jwtString);
    }

    @GetMapping("/me")
    public ResponseEntity<?> getCurrentUser(@AuthenticationPrincipal UserDetails userDetails) {
        if (userDetails == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("User not authenticated");
        }

        return userService.getCurrentUserByEmail(userDetails.getUsername())
                .<ResponseEntity<?>>map(user -> ResponseEntity.ok(ApiResponse.success(user)))
                .orElseGet(() -> ResponseEntity.status(HttpStatus.NOT_FOUND).body("User not found"));
    }

    @PostMapping("/employees")
    @PreAuthorize("hasRole('SUPERVISOR')")
    public ResponseEntity<?> createEmployee(@RequestBody CreateEmployeeDTO req) {
        UserResponse res = userService.createEmployee(req);
        return ResponseEntity.ok(com.example.restservice.dto.ApiResponse.success(200, "Success", res));
    }

    @GetMapping("/dashboard/{employeeId}")
    public ResponseEntity<?> getDashboardSummary(
            @PathVariable Long employeeId) {
        DashboardSummaryResponse res = assessmentService.getEmployeeDashboard(employeeId);
        return ResponseEntity.ok(com.example.restservice.dto.ApiResponse.success(200, "Success", res));
    }
}