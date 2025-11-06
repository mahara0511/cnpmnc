package com.example.restservice.ai.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AISuggestionRequestDto {
    private Long criteriaId;
    private Integer score;
    private String criteriaName;
    private String criteriaDescription;
    private String employeeName;
    private String previousComment;
}
