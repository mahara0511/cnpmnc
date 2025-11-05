package com.example.restservice.dto.assessment;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CreateAssessmentRequestDto {
    private Long employeeId;
    private List<ScoreRequestDto> scores;
}
