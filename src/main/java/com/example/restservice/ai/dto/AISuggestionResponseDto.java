package com.example.restservice.ai.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AISuggestionResponseDto {
    private String suggestedComment;
    private List<String> strengths;
    private List<String> improvements;
    private List<String> actionableRecommendations;
    private String tone;
}
