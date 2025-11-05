package com.example.restservice.dto.assessment;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CriteriaDto {
    private Long criteriaId;
    private String criteriaName;
    private String description;
    private Integer weight;
    private String category;
}