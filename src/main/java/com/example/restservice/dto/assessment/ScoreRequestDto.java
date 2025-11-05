package com.example.restservice.dto.assessment;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ScoreRequestDto {
    private Long criteriaId;
    private Long score;
    private String comment;
}
