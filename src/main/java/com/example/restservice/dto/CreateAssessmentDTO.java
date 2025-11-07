package com.example.restservice.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CreateAssessmentDTO {
    private Long employeeId;
    private List<CriteriaScoreInputDTO> scores;
}
