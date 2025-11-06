package com.example.restservice.ai.controller;

import com.example.restservice.ai.dto.*;
import com.example.restservice.ai.service.GeminiAIService;
import com.example.restservice.dto.ApiResponse;
import com.example.restservice.entity.Criteria;
import com.example.restservice.repository.CriteriaRepository;
import com.example.restservice.security.CustomUserDetails;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@Tag(name = "AI Assistant", description = "AI-powered features for performance assessment")
@RequestMapping("/ai")
public class AIController {
    
    private final GeminiAIService geminiAIService;
    private final CriteriaRepository criteriaRepository;
    
    public AIController(GeminiAIService geminiAIService,
                       CriteriaRepository criteriaRepository) {
        this.geminiAIService = geminiAIService;
        this.criteriaRepository = criteriaRepository;
    }
    
    @Operation(summary = "Get AI comment suggestions", 
              description = "Generate AI-powered assessment comment suggestions based on criteria and score")
    @PostMapping("/suggest-comment")
    @PreAuthorize("hasRole('SUPERVISOR')")
    public ResponseEntity<ApiResponse<AISuggestionResponseDto>> suggestComment(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @RequestBody AISuggestionRequestDto request) {
        
        log.info("Generating AI comment suggestions for criteria: {}", request.getCriteriaId());
        
        // Enrich request with criteria details
        Criteria criteria = criteriaRepository.findById(request.getCriteriaId())
            .orElseThrow(() -> new RuntimeException("Criteria not found"));
        
        request.setCriteriaName(criteria.getName());
        request.setCriteriaDescription(criteria.getDescription());
        
        // Generate suggestions
        AISuggestionResponseDto suggestions = geminiAIService.generateCommentSuggestions(request);
        
        return ResponseEntity.ok(ApiResponse.success(200, "AI suggestions generated successfully", suggestions));
    }
    
    @Operation(summary = "Chat with AI assistant",
              description = "Interact with AI assistant to get help with assessments")
    @PostMapping("/chat")
    @PreAuthorize("hasRole('SUPERVISOR')")
    public ResponseEntity<ApiResponse<ChatResponseDto>> chat(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @RequestBody ChatRequestDto request) {
        
        log.info("Processing AI chat request from supervisor {}: {}", 
            userDetails.getId(), request.getMessage());
        
        // Service will build context and get AI response
        ChatResponseDto response = geminiAIService.chat(userDetails.getId(), request);
        
        return ResponseEntity.ok(ApiResponse.success(200, "AI response generated", response));
    }
}
