package com.example.restservice.controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.example.restservice.service.AssessmentService;
import com.example.restservice.dto.assessment.AssessmentResponseDto;
import com.example.restservice.dto.assessment.CreateAssessmentRequestDto;
import com.example.restservice.dto.assessment.UpdateAssessmentStatusRequestDto;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import com.example.restservice.dto.ApiResponse;
import com.example.restservice.security.CustomUserDetails;
import java.util.List;
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

}