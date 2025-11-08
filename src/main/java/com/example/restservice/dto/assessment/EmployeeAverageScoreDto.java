package com.example.restservice.dto.assessment;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class EmployeeAverageScoreDto {
    private EmployeeDto employee;
    private Double averageScore;
    private Long totalAssessments;
}
