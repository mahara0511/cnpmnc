package com.example.restservice.response;

import com.example.restservice.common.enums.Category;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CriteriaResponseDTO {
    private Long id;
    private String name;
    private String description;
    private int weight;
    private Category category;
}
