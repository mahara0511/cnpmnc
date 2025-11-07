package com.example.restservice.service;

import com.example.restservice.common.enums.AssessmentStatus;
import com.example.restservice.dto.*;
import com.example.restservice.entity.*;
import com.example.restservice.repository.AssessmentRepository;
import com.example.restservice.repository.CriteriaRepository;
import com.example.restservice.repository.EmployeeRepository;
import com.example.restservice.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class AssessmentService {

    @Autowired
    private AssessmentRepository assessmentRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private EmployeeRepository employeeRepository;

    @Autowired
    private CriteriaRepository criteriaRepository;

    @Transactional(readOnly = true)
    public List<AssessmentResponseDTO> getAllAssessments(String userEmail, Long supervisorId, 
                                                         Long employeeId, String statusStr,
                                                         Integer page, Integer limit) {
        // Get current user to determine role
        User currentUser = userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new RuntimeException("User not found"));

        List<Assessment> assessments;

        if (currentUser instanceof Employee) {
            // EMPLOYEE role: can optionally filter by supervisorId, and only get Published assessments
            assessments = assessmentRepository.findBySupervisorIdAndPublished(supervisorId);
        } else if (currentUser instanceof Supervisor) {
            // SUPERVISOR role: can optionally filter by employeeId and status
            AssessmentStatus status = null;
            if (statusStr != null && !statusStr.isEmpty()) {
                try {
                    status = AssessmentStatus.valueOf(statusStr);
                } catch (IllegalArgumentException e) {
                    throw new IllegalArgumentException("Invalid status value: " + statusStr);
                }
            }
            
            assessments = assessmentRepository.findByEmployeeIdAndStatus(employeeId, status);
        } else {
            throw new IllegalArgumentException("User must be either EMPLOYEE or SUPERVISOR");
        }

        // Apply pagination
        int pageNumber = (page != null && page > 0) ? page : 1;
        int pageSize = (limit != null && limit > 0) ? limit : 10;
        int startIndex = (pageNumber - 1) * pageSize;
        int endIndex = Math.min(startIndex + pageSize, assessments.size());

        // Return paginated results
        if (startIndex >= assessments.size()) {
            return new ArrayList<>();
        }

        return assessments.subList(startIndex, endIndex).stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    @Transactional
    public AssessmentResponseDTO createAssessment(String supervisorEmail, CreateAssessmentDTO request) {
        // Get the supervisor (current user)
        User currentUser = userRepository.findByEmail(supervisorEmail)
                .orElseThrow(() -> new RuntimeException("User not found"));

        if (!(currentUser instanceof Supervisor)) {
            throw new IllegalArgumentException("Only supervisors can create assessments");
        }

        Supervisor supervisor = (Supervisor) currentUser;

        // Get the employee
        Employee employee = employeeRepository.findById(request.getEmployeeId())
                .orElseThrow(() -> new RuntimeException("Employee not found with id: " + request.getEmployeeId()));

        // Create the assessment
        Assessment assessment = new Assessment();
        assessment.setSupervisor(supervisor);
        assessment.setEmployee(employee);
        assessment.setStatus(AssessmentStatus.InProgress);

        // Create IsBelongTo entities for each criteria score
        List<IsBelongTo> isBelongToList = new ArrayList<>();
        double totalWeightedScore = 0.0;
        int totalWeight = 0;

        for (CriteriaScoreInputDTO scoreInput : request.getScores()) {
            Criteria criteria = criteriaRepository.findById(scoreInput.getCriteriaId())
                    .orElseThrow(() -> new RuntimeException("Criteria not found with id: " + scoreInput.getCriteriaId()));

            IsBelongTo isBelongTo = new IsBelongTo();
            isBelongTo.setScore(scoreInput.getScore());
            isBelongTo.setComment(scoreInput.getComment());
            isBelongTo.setCriteria(criteria);
            isBelongTo.setAssessment(assessment);

            isBelongToList.add(isBelongTo);

            // Calculate weighted score
            totalWeightedScore += scoreInput.getScore() * criteria.getWeight();
            totalWeight += criteria.getWeight();
        }

        // Calculate total score
        double totalScore = totalWeight > 0 ? totalWeightedScore / totalWeight : 0.0;
        assessment.setTotalScore(totalScore);
        assessment.setIsBelongTo(isBelongToList);

        // Save the assessment
        Assessment savedAssessment = assessmentRepository.save(assessment);

        // Convert to DTO and return
        return convertToDTO(savedAssessment);
    }

    private AssessmentResponseDTO convertToDTO(Assessment assessment) {
        AssessmentResponseDTO dto = new AssessmentResponseDTO();
        dto.setAssessmentId(assessment.getId());
        dto.setStatus(assessment.getStatus());
        dto.setTotalScore(assessment.getTotalScore());
        dto.setCreatedAt(assessment.getCreatedAt());

        // Convert supervisor
        if (assessment.getSupervisor() != null) {
            Supervisor supervisor = assessment.getSupervisor();
            dto.setSupervisor(new UserBasicDTO(
                supervisor.getId(),
                supervisor.getName(),
                supervisor.getEmail()
            ));
        }

        // Convert employee
        if (assessment.getEmployee() != null) {
            Employee employee = assessment.getEmployee();
            dto.setEmployee(new UserBasicDTO(
                employee.getId(),
                employee.getName(),
                employee.getEmail()
            ));
        }

        // Convert criteria scores
        if (assessment.getIsBelongTo() != null) {
            List<CriteriaScoreDTO> criteriaScores = assessment.getIsBelongTo().stream()
                .map(this::convertToCriteriaScoreDTO)
                .collect(Collectors.toList());
            dto.setCriteriaScores(criteriaScores);
        }

        return dto;
    }

    private CriteriaScoreDTO convertToCriteriaScoreDTO(IsBelongTo isBelongTo) {
        CriteriaScoreDTO scoreDTO = new CriteriaScoreDTO();
        scoreDTO.setScore(isBelongTo.getScore());
        scoreDTO.setComment(isBelongTo.getComment());

        // Convert criteria
        if (isBelongTo.getCriteria() != null) {
            Criteria criteria = isBelongTo.getCriteria();
            CriteriaDTO criteriaDTO = new CriteriaDTO(
                criteria.getId(),
                criteria.getName(),
                criteria.getDescription(),
                criteria.getWeight(),
                criteria.getCategory()
            );
            scoreDTO.setCriteria(criteriaDTO);
        }

        return scoreDTO;
    }
}
