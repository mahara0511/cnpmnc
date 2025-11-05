package com.example.restservice.dto.assessment;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;
import com.example.restservice.common.enums.Status;
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AssessmentResponseDto {
    private Long assessmentId;
    private SupervisorDto supervisor;
    private EmployeeDto employee;
    private Status status;
    private Double totalScore;
    private List<CriteriaScoreDto> criteriaScores;
    
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss'Z'")
    private LocalDateTime createdAt;
}