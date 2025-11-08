package com.example.restservice.ai.service;

import com.example.restservice.ai.dto.AISuggestionRequestDto;
import com.example.restservice.ai.dto.AISuggestionResponseDto;
import com.example.restservice.ai.dto.ChatRequestDto;
import com.example.restservice.ai.dto.ChatResponseDto;
import com.example.restservice.common.enums.Status;
import com.example.restservice.entity.Assessment;
import com.example.restservice.entity.Criteria;
import com.example.restservice.entity.Employee;
import com.example.restservice.repository.AssessmentRepository;
import com.example.restservice.repository.CriteriaRepository;
import com.example.restservice.repository.EmployeeRepository;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Slf4j
@Service
public class GeminiAIService {
    
    @Value("${gemini.api.key}")
    private String apiKey;
    
    @Value("${gemini.api.url}")
    private String apiUrl;
    
    private final RestTemplate restTemplate;
    private final ObjectMapper objectMapper;
    private final AssessmentRepository assessmentRepository;
    private final CriteriaRepository criteriaRepository;
    private final EmployeeRepository employeeRepository;
    
    public GeminiAIService(RestTemplate restTemplate, 
                          ObjectMapper objectMapper,
                          AssessmentRepository assessmentRepository,
                          CriteriaRepository criteriaRepository,
                          EmployeeRepository employeeRepository) {
        this.restTemplate = restTemplate;
        this.objectMapper = objectMapper;
        this.assessmentRepository = assessmentRepository;
        this.criteriaRepository = criteriaRepository;
        this.employeeRepository = employeeRepository;
    }
    
    public AISuggestionResponseDto generateCommentSuggestions(AISuggestionRequestDto request) {
        String prompt = buildCommentPrompt(request);
        String response = callGeminiAPI(prompt);
        return parseCommentResponse(response);
    }
    
    public ChatResponseDto chat(Long supervisorId, ChatRequestDto request) {
        String context = buildChatContext(supervisorId, request.getMessage());
        String prompt = buildChatPrompt(request, context);
        String response = callGeminiAPI(prompt);
        return parseChatResponse(response);
    }
    
    public ChatResponseDto employeeChat(Long employeeId, ChatRequestDto request) {
        String context = buildEmployeeChatContext(employeeId, request.getMessage());
        String prompt = buildEmployeeChatPrompt(request, context);
        String response = callGeminiAPI(prompt);
        return parseChatResponse(response);
    }
    
    private String buildCommentPrompt(AISuggestionRequestDto request) {
        return String.format("""
            You are an expert HR performance reviewer.
            
            Task: Generate a professional performance assessment comment.
            
            Context:
            - Criteria: %s
            - Description: %s
            - Employee: %s
            - Score: %d/100
            
            Score Guidelines (out of 100):
            - 90-100: Exceptional/Outstanding - Consistently exceeds expectations
            - 80-89: Excellent/Strong - Frequently exceeds expectations
            - 70-79: Good/Solid - Meets expectations consistently
            - 60-69: Satisfactory/Adequate - Meets most expectations
            - Below 60: Needs Improvement - Below expectations
            
            Return response in JSON format:
            {
              "suggestedComment": "Professional 2-3 sentence comment",
              "strengths": ["strength1", "strength2"],
              "improvements": ["improvement1", "improvement2"],
              "actionableRecommendations": ["rec1", "rec2", "rec3"],
              "tone": "positive"
            }
            
            Only return valid JSON, no additional text.
            """,
            request.getCriteriaName(),
            request.getCriteriaDescription(),
            request.getEmployeeName(),
            request.getScore()
        );
    }
    
    private String buildChatPrompt(ChatRequestDto request, String context) {
        return String.format("""
            You are an AI assistant helping supervisors with performance assessments.
            Provide clear, helpful, and professional responses.
            
            User Question: %s
            
            Context: %s
            
            IMPORTANT: Provide a detailed response in PLAIN TEXT ONLY. 
            Do NOT use any markdown formatting (no **, *, #, -, bullets, or code blocks).
            Do NOT use \\n for line breaks.
            Write naturally as if speaking directly to the person.
            """,
            request.getMessage(),
            context != null ? context : "No additional context"
        );
    }
    
    private String buildEmployeeChatPrompt(ChatRequestDto request, String context) {
        return String.format("""
            You are an AI assistant helping employees understand their performance assessments.
            Be supportive, constructive, and encouraging in your responses.
            
            Employee Question: %s
            
            Employee's Assessment Data: %s
            
            Guidelines for your response:
            - Provide insights about their performance trends
            - Suggest specific areas for improvement with actionable steps
            - Highlight strengths and achievements
            - Offer encouragement and motivation
            - Explain assessment criteria clearly
            
            IMPORTANT: Provide a supportive and detailed response in PLAIN TEXT ONLY.
            Do NOT use any markdown formatting (no **, *, #, -, bullets, or code blocks).
            Do NOT use \\n for line breaks.
            Write naturally as if speaking directly to the person in a friendly conversation.
            """,
            request.getMessage(),
            context != null ? context : "No assessment data available"
        );
    }
    
    private String callGeminiAPI(String prompt) {
        try {
            Map<String, Object> requestBody = Map.of(
                "contents", List.of(
                    Map.of("parts", List.of(
                        Map.of("text", prompt)
                    ))
                ),
                "generationConfig", Map.of(
                    "temperature", 0.7,
                    "maxOutputTokens", 2048
                )
            );
            
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            
            String url = apiUrl + "?key=" + apiKey;
            HttpEntity<Map<String, Object>> entity = new HttpEntity<>(requestBody, headers);
            
            ResponseEntity<String> response = restTemplate.exchange(
                url,
                HttpMethod.POST,
                entity,
                String.class
            );
            
            if (response.getStatusCode() == HttpStatus.OK) {
                JsonNode root = objectMapper.readTree(response.getBody());
                
                // Log response for debugging
                log.debug("Gemini API Response: {}", response.getBody());
                
                // Check if response has candidates
                JsonNode candidates = root.path("candidates");
                if (candidates.isMissingNode() || candidates.isEmpty()) {
                    log.error("No candidates in Gemini response: {}", response.getBody());
                    throw new RuntimeException("Gemini API returned no candidates");
                }
                
                // Safely extract text from response
                JsonNode candidate = candidates.get(0);
                if (candidate == null) {
                    throw new RuntimeException("First candidate is null");
                }
                
                JsonNode content = candidate.path("content");
                if (content.isMissingNode()) {
                    throw new RuntimeException("No content in candidate");
                }
                
                JsonNode parts = content.path("parts");
                if (parts.isMissingNode() || parts.isEmpty()) {
                    throw new RuntimeException("No parts in content");
                }
                
                JsonNode part = parts.get(0);
                if (part == null) {
                    throw new RuntimeException("First part is null");
                }
                
                String text = part.path("text").asText();
                if (text.isEmpty()) {
                    throw new RuntimeException("Text content is empty");
                }
                
                return text;
            }
            
            log.error("Gemini API failed with status: {}", response.getStatusCode());
            throw new RuntimeException("Gemini API call failed: " + response.getStatusCode());
            
        } catch (Exception e) {
            log.error("Error calling Gemini API: {}", e.getMessage(), e);
            
            // Log full error details
            if (e.getMessage() != null) {
                log.error("Error details: {}", e.getMessage());
            }
            
            // Return a helpful fallback message instead of throwing exception
            return "I apologize, but I'm unable to process your request at the moment. " +
                   "This could be due to a temporary API issue. Please try again in a moment. " +
                   "If the problem persists, please contact support.";
        }
    }
    
    private AISuggestionResponseDto parseCommentResponse(String response) {
        try {
            String jsonStr = cleanJsonResponse(response);
            JsonNode node = objectMapper.readTree(jsonStr);
            
            return AISuggestionResponseDto.builder()
                .suggestedComment(node.path("suggestedComment").asText())
                .strengths(parseStringList(node.path("strengths")))
                .improvements(parseStringList(node.path("improvements")))
                .actionableRecommendations(parseStringList(node.path("actionableRecommendations")))
                .tone(node.path("tone").asText("neutral"))
                .build();
                
        } catch (Exception e) {
            log.error("Error parsing comment response", e);
            return AISuggestionResponseDto.builder()
                .suggestedComment("Unable to generate suggestion. Please try again.")
                .strengths(new ArrayList<>())
                .improvements(new ArrayList<>())
                .actionableRecommendations(new ArrayList<>())
                .tone("neutral")
                .build();
        }
    }
    
    private ChatResponseDto parseChatResponse(String response) {
        // Simply return the plain text response from Gemini
        return ChatResponseDto.builder()
            .reply(response.trim())
            .suggestedActions(new ArrayList<>())
            .relevantData(new HashMap<>())
            .build();
    }
    
    private String cleanJsonResponse(String response) {
        String cleaned = response.trim();
        if (cleaned.startsWith("```json")) cleaned = cleaned.substring(7);
        if (cleaned.startsWith("```")) cleaned = cleaned.substring(3);
        if (cleaned.endsWith("```")) cleaned = cleaned.substring(0, cleaned.length() - 3);
        return cleaned.trim();
    }
    
    private List<String> parseStringList(JsonNode node) {
        List<String> result = new ArrayList<>();
        if (node.isArray()) {
            node.forEach(item -> result.add(item.asText()));
        }
        return result;
    }
    
    private String buildChatContext(Long supervisorId, String message) {
        StringBuilder context = new StringBuilder();
        String lowerMessage = message.toLowerCase();
        
        // Get supervisor's assessments
        List<Assessment> supervisorAssessments = assessmentRepository.findBySupervisorId(supervisorId);
        
        // Add supervisor context
        context.append(String.format("Supervisor Context:\n- Total Assessments Created: %d\n\n", 
            supervisorAssessments.size()));
        
        // 1. Detect assessment ID (e.g., "assessment #1", "assessment 1")
        Pattern assessmentPattern = Pattern.compile("assessment\\s*#?(\\d+)");
        Matcher assessmentMatcher = assessmentPattern.matcher(lowerMessage);
        if (assessmentMatcher.find()) {
            Long assessmentId = Long.parseLong(assessmentMatcher.group(1));
            Assessment assessment = assessmentRepository.findById(assessmentId).orElse(null);
            
            if (assessment != null) {
                // Verify supervisor owns this assessment
                if (assessment.getSupervisor().getId().equals(supervisorId)) {
                    context.append(String.format(
                        "Assessment #%d Details:\n" +
                        "- Employee: %s\n" +
                        "- Status: %s\n" +
                        "- Total Score: %.2f/100\n" +
                        "- Criteria Evaluated: %d\n" +
                        "- Created: %s\n\n",
                        assessment.getId(),
                        assessment.getEmployee().getName(),
                        assessment.getStatus(),
                        assessment.getTotalScore(),
                        assessment.getIsBelongTo().size(),
                        assessment.getCreatedAt()
                    ));
                }
            }
        }
        
                
        // 2. Detect employee name and build context using Stream API
        employeeRepository.findAll().stream()
            .filter(employee -> employee.getName() != null)
            .filter(employee -> lowerMessage.contains(employee.getName().toLowerCase()))
            .findFirst()
            .ifPresent(employee -> {
                List<Assessment> mySupervisorAssessments = assessmentRepository
                    .findByEmployeeId(employee.getId()).stream()
                    .filter(assessment -> assessment.getSupervisor().getId().equals(supervisorId))
                    .toList();
                
                double avgScore = mySupervisorAssessments.stream()
                    .mapToDouble(Assessment::getTotalScore)
                    .average()
                    .orElse(0.0);
                
                context.append(String.format(
                    "Employee: %s\n" +
                    "- Email: %s\n" +
                    "- Your Assessments: %d\n" +
                    "- Average Score: %.2f/100\n",
                    employee.getName(),
                    employee.getEmail(),
                    mySupervisorAssessments.size(),
                    avgScore
                ));
                
                mySupervisorAssessments.stream()
                    .reduce((first, second) -> second) // Get last element
                    .ifPresent(latest -> context.append(String.format(
                        "- Latest Assessment: %.2f/100 (%s)\n\n",
                        latest.getTotalScore(),
                        latest.getStatus()
                    )));
            });
        
        // 3. Detect criteria from database
        criteriaRepository.findAll().stream()
            .filter(criteria -> criteria.getName() != null)
            .filter(criteria -> lowerMessage.contains(criteria.getName().toLowerCase()))
            .findFirst()
            .ifPresent(criteria -> context.append(String.format(
                "Criteria: %s\n" +
                "- Description: %s\n" +
                "- Weight: %d/5\n" +
                "- Category: %s\n" +
                "- Scoring Guidelines:\n" +
                "  * 90-100: Exceptional - Consistently exceeds expectations\n" +
                "  * 80-89: Excellent - Regularly exceeds expectations\n" +
                "  * 70-79: Good - Meets and sometimes exceeds expectations\n" +
                "  * 60-69: Satisfactory - Meets basic expectations\n" +
                "  * Below 60: Needs Improvement - Requires development\n\n",
                criteria.getName(),
                criteria.getDescription(),
                criteria.getWeight(),
                criteria.getCategory()
            )));
        
        // 4. General statistics if no specific context detected
        
        // 3. Detect criteria from database
        List<Criteria> allCriteria = criteriaRepository.findAll();
        
        for (Criteria criteria : allCriteria) {
            if (lowerMessage.contains(criteria.getName().toLowerCase())) {
                context.append(String.format(
                    "Criteria: %s\n" +
                    "- Description: %s\n" +
                    "- Weight: %d/5\n" +
                    "- Category: %s\n" +
                    "- Scoring Guidelines:\n" +
                    "  * 90-100: Exceptional - Consistently exceeds expectations\n" +
                    "  * 80-89: Excellent - Regularly exceeds expectations\n" +
                    "  * 70-79: Good - Meets and sometimes exceeds expectations\n" +
                    "  * 60-69: Satisfactory - Meets basic expectations\n" +
                    "  * Below 60: Needs Improvement - Requires development\n\n",
                    criteria.getName(),
                    criteria.getDescription(),
                    criteria.getWeight(),
                    criteria.getCategory()
                ));
                break;
            }
        }
        
        // 4. General statistics if no specific context
        if (!lowerMessage.contains("assessment") && 
            !containsEmployeeName(lowerMessage, supervisorAssessments)) {
            
            List<Assessment> published = supervisorAssessments.stream()
                .filter(a -> a.getStatus() == Status.Published)
                .toList();
            
            List<Assessment> inProgress = supervisorAssessments.stream()
                .filter(a -> a.getStatus() == Status.InProgress)
                .toList();
            
            context.append(String.format(
                "Your Assessment Summary:\n" +
                "- Published: %d\n" +
                "- In Progress: %d\n",
                published.size(),
                inProgress.size()
            ));
            
            if (!supervisorAssessments.isEmpty()) {
                double avgScore = supervisorAssessments.stream()
                    .mapToDouble(Assessment::getTotalScore)
                    .average()
                    .orElse(0.0);
                context.append(String.format("- Average Score: %.2f/100\n\n", avgScore));
            }
        }
        
        log.info("Built context for chat: {}", context.toString());
        return context.toString();
    }
    
    private String buildEmployeeChatContext(Long employeeId, String message) {
        StringBuilder context = new StringBuilder();
        String lowerMessage = message.toLowerCase();
        
        // Get employee's own assessments
        List<Assessment> employeeAssessments = assessmentRepository.findByEmployeeId(employeeId);
        
        if (employeeAssessments.isEmpty()) {
            context.append("No assessments found for you yet.\n");
            return context.toString();
        }
        
        // Add employee's assessment summary
        long totalAssessments = employeeAssessments.size();
        long publishedCount = employeeAssessments.stream()
            .filter(a -> a.getStatus() == Status.Published)
            .count();
        long inProgressCount = employeeAssessments.stream()
            .filter(a -> a.getStatus() == Status.InProgress)
            .count();
        
        double avgScore = employeeAssessments.stream()
            .mapToDouble(Assessment::getTotalScore)
            .average()
            .orElse(0.0);
        
        context.append(String.format(
            "Your Performance Summary:\n" +
            "- Total Assessments: %d\n" +
            "- Published: %d\n" +
            "- In Progress: %d\n" +
            "- Average Score: %.2f/100\n\n",
            totalAssessments,
            publishedCount,
            inProgressCount,
            avgScore
        ));
        
        // 1. Detect if asking about specific assessment ID
        Pattern assessmentPattern = Pattern.compile("assessment\\s*#?(\\d+)");
        Matcher assessmentMatcher = assessmentPattern.matcher(lowerMessage);
        if (assessmentMatcher.find()) {
            Long assessmentId = Long.parseLong(assessmentMatcher.group(1));
            employeeAssessments.stream()
                .filter(a -> a.getId().equals(assessmentId))
                .findFirst()
                .ifPresent(assessment -> context.append(String.format(
                    "Assessment #%d Details:\n" +
                    "- Supervisor: %s\n" +
                    "- Status: %s\n" +
                    "- Total Score: %.2f/100\n" +
                    "- Criteria Evaluated: %d\n" +
                    "- Created: %s\n\n",
                    assessment.getId(),
                    assessment.getSupervisor().getName(),
                    assessment.getStatus(),
                    assessment.getTotalScore(),
                    assessment.getIsBelongTo().size(),
                    assessment.getCreatedAt()
                )));
        }
        
        // 2. Detect if asking about specific criteria
        criteriaRepository.findAll().stream()
            .filter(criteria -> criteria.getName() != null)
            .filter(criteria -> lowerMessage.contains(criteria.getName().toLowerCase()))
            .findFirst()
            .ifPresent(criteria -> {
                context.append(String.format(
                    "About '%s' Criterion:\n" +
                    "- Description: %s\n" +
                    "- Weight: %d/5\n" +
                    "- Category: %s\n\n",
                    criteria.getName(),
                    criteria.getDescription(),
                    criteria.getWeight(),
                    criteria.getCategory()
                ));
                
                // Show employee's performance on this criteria
                context.append("Your scores on this criteria:\n");
                employeeAssessments.stream()
                    .flatMap(assessment -> assessment.getIsBelongTo().stream())
                    .filter(score -> score.getCriteria().getId().equals(criteria.getId()))
                    .forEach(score -> context.append(String.format(
                        "- Assessment #%d: %.2f/100\n",
                        score.getAssessment().getId(),
                        score.getScore()
                    )));
                context.append("\n");
            });
        
        // 3. Show latest assessment details if no specific query
        if (!assessmentMatcher.find() && !lowerMessage.contains("criteria")) {
            employeeAssessments.stream()
                .reduce((first, second) -> second) // Get last element
                .ifPresent(latest -> context.append(String.format(
                    "Latest Assessment:\n" +
                    "- Supervisor: %s\n" +
                    "- Score: %.2f/100\n" +
                    "- Status: %s\n\n",
                    latest.getSupervisor().getName(),
                    latest.getTotalScore(),
                    latest.getStatus()
                )));
        }
        
        log.info("Built employee chat context: {}", context.toString());
        return context.toString();
    }
    
    private boolean containsEmployeeName(String message, List<Assessment> assessments) {
        return employeeRepository.findAll().stream()
            .filter(employee -> employee.getName() != null)
            .anyMatch(employee -> message.contains(employee.getName().toLowerCase()));
    }
}
