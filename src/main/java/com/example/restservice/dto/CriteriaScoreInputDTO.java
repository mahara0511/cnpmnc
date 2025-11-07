package com.example.restservice.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CriteriaScoreInputDTO {
    private Long criteriaId;
    private Long score;
    private String comment;
}
