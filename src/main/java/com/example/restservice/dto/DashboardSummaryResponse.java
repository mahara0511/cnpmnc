package com.example.restservice.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class DashboardSummaryResponse {
    private Long employeeId;
    private String employeeName;
    private double overallAvgScore;
    private long totalAssessments;
    private List<DashboardResponse> monthlyStats;
}