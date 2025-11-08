package com.example.restservice.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.example.restservice.service.AssessmentService;
import com.example.restservice.dto.assessment.AssessmentResponseDto;
import com.example.restservice.dto.assessment.CreateAssessmentRequestDto;
import com.example.restservice.dto.assessment.UpdateAssessmentStatusRequestDto;
import com.example.restservice.dto.assessment.EmployeeAverageScoreDto;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import com.example.restservice.dto.ApiResponse;
import com.example.restservice.security.CustomUserDetails;
import java.util.List;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import io.swagger.v3.oas.annotations.Operation;

import com.example.restservice.common.enums.Status;
@RestController
@Tag(name = "Assessment", description = "Assessment API")
@RequestMapping("/assessments")
public class AssessmentController {
  private final AssessmentService assessmentService;

  public AssessmentController(AssessmentService assessmentService) {
    this.assessmentService = assessmentService;
  }

  @Operation(summary = "Get assessments for supervisor with optional filters", description = "Retrieve assessments created by the authenticated supervisor. Optional filters include employeeId and status.")
  @GetMapping("/supervisor")
  @PreAuthorize("hasRole('SUPERVISOR')")
  public ResponseEntity<ApiResponse<List<AssessmentResponseDto>>> getSupervisorAssessments(
          @AuthenticationPrincipal CustomUserDetails userDetails,
          @RequestParam(required = false) Long employeeId,
          @RequestParam(required = false) Status status) {
    Long supervisorId = userDetails.getId();
    List<AssessmentResponseDto> assessments = assessmentService.getAssessmentsBySupervisor(supervisorId, employeeId, status);
    return ResponseEntity.ok(ApiResponse.success(200, "Success", assessments));
  }

  @Operation(summary = "Get assessments for employee with optional supervisor filter", description = "Retrieve assessments assigned to the authenticated employee. Optional filter includes supervisorId.")
  @GetMapping("/employee")
  @PreAuthorize("hasRole('EMPLOYEE')")
  public ResponseEntity<ApiResponse<List<AssessmentResponseDto>>> getEmployeeAssessments(
          @AuthenticationPrincipal CustomUserDetails userDetails,
          @RequestParam(required = false) Long supervisorId) {
    Long employeeId = userDetails.getId();
    List<AssessmentResponseDto> assessments = assessmentService.getAssessmentsByEmployee(employeeId, supervisorId);
    return ResponseEntity.ok(ApiResponse.success(200, "Success", assessments));
  }

  @Operation(summary = "Create assessment", description = "Create a new assessment for an employee. Only supervisors can create assessments.")
  @PostMapping
  @PreAuthorize("hasRole('SUPERVISOR')")
  public ResponseEntity<ApiResponse<AssessmentResponseDto>> createAssessment(
          @AuthenticationPrincipal CustomUserDetails userDetails,
          @RequestBody CreateAssessmentRequestDto request) {
    Long supervisorId = userDetails.getId();
    AssessmentResponseDto assessment = assessmentService.createAssessment(supervisorId, request);
    return ResponseEntity.status(HttpStatus.CREATED)
            .body(ApiResponse.success(201, "Success", assessment));
  }

  @Operation(summary = "Update assessment status", description = "Update the status of an assessment. Only supervisors can update assessment status.")
  @PatchMapping("/{assessmentId}/status")
  @PreAuthorize("hasRole('SUPERVISOR')")
  public ResponseEntity<ApiResponse<AssessmentResponseDto>> updateAssessmentStatus(
          @AuthenticationPrincipal CustomUserDetails userDetails,
          @PathVariable Long assessmentId,
          @RequestBody UpdateAssessmentStatusRequestDto request) {
            
    Long supervisorId = userDetails.getId();
    AssessmentResponseDto assessment = assessmentService.updateAssessmentStatus(supervisorId, assessmentId, request);
    return ResponseEntity.ok(ApiResponse.success(200, "Success", assessment));
  }

  @Operation(summary = "Get assessment by ID", description = "Retrieve detailed information of a specific assessment by its ID. Supervisors can view their own assessments, employees can view their assigned assessments.")
  @GetMapping("/{assessmentId}")
  @PreAuthorize("hasAnyRole('SUPERVISOR', 'EMPLOYEE')")
  public ResponseEntity<ApiResponse<AssessmentResponseDto>> getAssessmentById(
          @AuthenticationPrincipal CustomUserDetails userDetails,
          @PathVariable Long assessmentId) {
    AssessmentResponseDto assessment = assessmentService.getAssessmentById(userDetails.getId(), assessmentId);
    return ResponseEntity.ok(ApiResponse.success(200, "Success", assessment));
  }

  @Operation(summary = "Update assessment", description = "Update an existing assessment including criteria scores. Can add new criteria, update existing scores, or remove criteria. Only supervisors can update assessments.")
  @PutMapping("/{assessmentId}")
  @PreAuthorize("hasRole('SUPERVISOR')")
  public ResponseEntity<ApiResponse<AssessmentResponseDto>> updateAssessment(
          @AuthenticationPrincipal CustomUserDetails userDetails,
          @PathVariable Long assessmentId,
          @RequestBody CreateAssessmentRequestDto request) {
    Long supervisorId = userDetails.getId();
    AssessmentResponseDto assessment = assessmentService.updateAssessment(supervisorId, assessmentId, request);
    return ResponseEntity.ok(ApiResponse.success(200, "Assessment updated successfully", assessment));
  }

  @Operation(summary = "Get employee average scores", description = "Get average assessment scores for all employees within a date range. Employees without assessments in the period will have an average score of -1. Default date range is the last 30 days. Results are sorted by average score (descending by default), with total assessments as a tiebreaker.")
  @GetMapping("/employee-average")
  @PreAuthorize("hasRole('SUPERVISOR')")
  public ResponseEntity<ApiResponse<List<EmployeeAverageScoreDto>>> getEmployeeAverageScores(
          @RequestParam(required = false) String startDate,
          @RequestParam(required = false) String endDate,
          @RequestParam(required = false, defaultValue = "desc") String sort) {
    
    LocalDateTime start;
    LocalDateTime end;
    
    // Parse dates or use defaults
    if (endDate == null || endDate.isEmpty()) {
      end = LocalDateTime.now();
    } else {
      try {
        end = LocalDateTime.parse(endDate + "T23:59:59");
      } catch (Exception e) {
        return ResponseEntity.badRequest()
                .body(ApiResponse.error(400, "Invalid endDate format. Use yyyy-MM-dd"));
      }
    }
    
    if (startDate == null || startDate.isEmpty()) {
      start = end.minusDays(30);
    } else {
      try {
        start = LocalDateTime.parse(startDate + "T00:00:00");
      } catch (Exception e) {
        return ResponseEntity.badRequest()
                .body(ApiResponse.error(400, "Invalid startDate format. Use yyyy-MM-dd"));
      }
    }
    
    // Validate startDate <= endDate
    if (start.isAfter(end)) {
      return ResponseEntity.badRequest()
              .body(ApiResponse.error(400, "startDate must be less than or equal to endDate"));
    }
    
    // Validate sort parameter
    if (!sort.equals("asc") && !sort.equals("desc")) {
      return ResponseEntity.badRequest()
              .body(ApiResponse.error(400, "Invalid sort parameter. Must be 'asc' or 'desc'"));
    }
    
    List<EmployeeAverageScoreDto> averageScores = assessmentService.getEmployeesAverageScore(start, end, sort);
    return ResponseEntity.ok(ApiResponse.success(200, "Success", averageScores));
  }


}