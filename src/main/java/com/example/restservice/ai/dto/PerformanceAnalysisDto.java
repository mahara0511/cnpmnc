package com.example.restservice.ai.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.Map;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PerformanceAnalysisDto {
    private Long employeeId;
    private String employeeName;
    private Map<String, TrendData> categoryTrends;
    private String overallTrend;
    private Double trendPercentage;
    private List<String> keyStrengths;
    private List<String> areasForImprovement;
    private List<String> insights;
    private ComparisonData teamComparison;
    private List<String> recommendations;
    private String developmentPlan;
    
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class TrendData {
        private String category;
        private Double currentAverage;
        private Double previousAverage;
        private Double changePercentage;
        private String trend;
    }
    
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ComparisonData {
        private Double employeeAverage;
        private Double teamAverage;
        private Double difference;
        private String performanceLevel;
    }
}
