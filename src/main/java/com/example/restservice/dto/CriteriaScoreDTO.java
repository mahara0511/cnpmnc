package com.example.restservice.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CriteriaScoreDTO {
    private CriteriaDTO criteria;
    private Long score;
    private String comment;
}
