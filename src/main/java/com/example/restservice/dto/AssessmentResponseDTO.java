package com.example.restservice.dto;

import com.example.restservice.common.enums.AssessmentStatus;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class AssessmentResponseDTO {
    private Long assessmentId;
    private UserBasicDTO supervisor;
    private UserBasicDTO employee;
    private AssessmentStatus status;
    private Double totalScore;
    private List<CriteriaScoreDTO> criteriaScores;
    private Instant createdAt;
}
