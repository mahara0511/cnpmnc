package com.example.restservice.controller;

import org.springframework.security.core.userdetails.User;
import com.example.restservice.dto.ApiResponse;
import com.example.restservice.dto.AssessmentResponseDTO;
import com.example.restservice.dto.CreateAssessmentDTO;
import com.example.restservice.service.AssessmentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;

import jakarta.validation.Valid;
import java.util.List;

@RestController
@Tag(name = "Assessment Management", description = "Endpoints for assessment management")
@RequestMapping("/assessment")
public class AssessmentController {

    @Autowired
    private AssessmentService assessmentService;

    public AssessmentController() {}

    @GetMapping("/all")
    @Operation(summary = "Get all assessments with role-based filtering",
               description = "EMPLOYEE: Can optionally filter by supervisorId, returns only Published assessments. " +
                           "SUPERVISOR: Can optionally filter by employeeId and status.")
    public ResponseEntity<ApiResponse<List<AssessmentResponseDTO>>> getAllAssessments(
            @Parameter(hidden = true) @AuthenticationPrincipal User user,
            @Parameter(description = "Supervisor ID (optional filter for EMPLOYEE role)")
            @RequestParam(required = false) Long supervisorId,
            @Parameter(description = "Employee ID (optional filter for SUPERVISOR role)")
            @RequestParam(required = false) Long employeeId,
            @Parameter(description = "Assessment status filter (optional for SUPERVISOR role)")
            @RequestParam(required = false) String status,
            @Parameter(description = "Page number (default: 1)")
            @RequestParam(required = false, defaultValue = "1") Integer page,
            @Parameter(description = "Number of items per page (default: 10)")
            @RequestParam(required = false, defaultValue = "10") Integer limit) {
        
        try {
            if (user == null) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                        .body(ApiResponse.error(HttpStatus.UNAUTHORIZED.value(), 
                              "User not authenticated"));
            }

            String userEmail = user.getUsername();
            List<AssessmentResponseDTO> assessments = assessmentService.getAllAssessments(
                    userEmail, supervisorId, employeeId, status, page, limit);

            return ResponseEntity.ok(ApiResponse.success(
                    HttpStatus.OK.value(),
                    "Success",
                    assessments
            ));

        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(ApiResponse.error(HttpStatus.BAD_REQUEST.value(), e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.error(HttpStatus.INTERNAL_SERVER_ERROR.value(), 
                          "Internal server error: " + e.getMessage()));
        }
    }

    @PostMapping
    @Operation(summary = "Create a new assessment",
               description = "Only supervisors can create assessments. Creates an assessment with status 'InProgress'.")
    public ResponseEntity<ApiResponse<AssessmentResponseDTO>> createAssessment(
            @Parameter(hidden = true) @AuthenticationPrincipal org.springframework.security.core.userdetails.User user,
            @Valid @RequestBody CreateAssessmentDTO request) {
        
        try {
            if (user == null) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                        .body(ApiResponse.error(HttpStatus.UNAUTHORIZED.value(), 
                              "User not authenticated"));
            }

            String supervisorEmail = user.getUsername();
            AssessmentResponseDTO assessment = assessmentService.createAssessment(supervisorEmail, request);

            return ResponseEntity.status(HttpStatus.CREATED)
                    .body(ApiResponse.success(
                            HttpStatus.CREATED.value(),
                            "Success",
                            assessment
                    ));

        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(ApiResponse.error(HttpStatus.BAD_REQUEST.value(), e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.error(HttpStatus.INTERNAL_SERVER_ERROR.value(), 
                          "Internal server error: " + e.getMessage()));
        }
    }
}