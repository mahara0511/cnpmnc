package com.example.restservice.response;

import com.example.restservice.common.enums.Category;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CriteriaDetailResponse {
    private Long criteriaId;
    private String criteriaName;
    private String description;
    private int weight;
    private Category category;
}
