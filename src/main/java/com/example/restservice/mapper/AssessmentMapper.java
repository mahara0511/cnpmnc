package com.example.restservice.mapper;

import com.example.restservice.dto.assessment.AssessmentResponseDto;
import com.example.restservice.dto.assessment.CriteriaDto;
import com.example.restservice.dto.assessment.CriteriaScoreDto;
import com.example.restservice.dto.assessment.EmployeeDto;
import com.example.restservice.dto.assessment.SupervisorDto;
import com.example.restservice.entity.Assessment;
import com.example.restservice.entity.IsBelongTo;
import org.springframework.stereotype.Component;

import java.util.stream.Collectors;

@Component
public class AssessmentMapper {
    public AssessmentResponseDto toDto(Assessment assessment) {
        if (assessment == null) {
            return null;
        }

        return AssessmentResponseDto.builder()
                .assessmentId(assessment.getId())
                .supervisor(SupervisorDto.builder()
                        .id(assessment.getSupervisor().getId())
                        .name(assessment.getSupervisor().getName())
                        .email(assessment.getSupervisor().getEmail())
                        .build())
                .employee(EmployeeDto.builder()
                        .id(assessment.getEmployee().getId())
                        .name(assessment.getEmployee().getName())
                        .email(assessment.getEmployee().getEmail())
                        .build())
                .status(assessment.getStatus())
                .totalScore(assessment.getTotalScore())
                .criteriaScores(assessment.getIsBelongTo().stream()
                        .map(this::mapCriteriaScore)
                        .collect(Collectors.toList()))
                .createdAt(assessment.getCreatedAt())
                .build();
    }

    private CriteriaScoreDto mapCriteriaScore(IsBelongTo isBelongTo) {
        return CriteriaScoreDto.builder()
                .criteria(CriteriaDto.builder()
                        .criteriaId(isBelongTo.getCriteria().getId())
                        .criteriaName(isBelongTo.getCriteria().getName())
                        .description(isBelongTo.getCriteria().getDescription())
                        .weight(isBelongTo.getCriteria().getWeight())
                        .category(isBelongTo.getCriteria().getCategory().name())
                        .build())
                .score(isBelongTo.getScore())
                .comment(isBelongTo.getComment())
                .build();
    }
}