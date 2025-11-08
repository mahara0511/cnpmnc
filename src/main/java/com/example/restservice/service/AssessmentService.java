package com.example.restservice.service;

import com.example.restservice.dto.DashboardResponse;
import com.example.restservice.dto.DashboardSummaryResponse;
import com.example.restservice.entity.Assessment;
import com.example.restservice.entity.IsBelongTo;
import com.example.restservice.entity.Employee;
import com.example.restservice.entity.Supervisor;
import com.example.restservice.entity.Criteria;
import com.example.restservice.dto.assessment.AssessmentResponseDto;
import com.example.restservice.dto.assessment.CreateAssessmentRequestDto;
import com.example.restservice.dto.assessment.UpdateAssessmentStatusRequestDto;
import com.example.restservice.dto.assessment.ScoreRequestDto;
import com.example.restservice.dto.assessment.EmployeeAverageScoreDto;
import com.example.restservice.dto.assessment.EmployeeDto;
import com.example.restservice.repository.AssessmentRepository;
import com.example.restservice.repository.EmployeeRepository;
import com.example.restservice.repository.UserRepository;
import com.example.restservice.repository.CriteriaRepository;
import com.example.restservice.mapper.AssessmentMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;
import java.util.Map;
import java.util.HashMap;
import com.example.restservice.common.enums.Status;
@Service
public class AssessmentService {
  private final AssessmentRepository assessmentRepository;
  private final AssessmentMapper assessmentMapper;
  private final EmployeeRepository employeeRepository;
  private final UserRepository userRepository;
  private final CriteriaRepository criteriaRepository;

  public AssessmentService(AssessmentRepository assessmentRepository, AssessmentMapper assessmentMapper,
                           EmployeeRepository employeeRepository, UserRepository userRepository,
                           CriteriaRepository criteriaRepository) {
    this.assessmentRepository = assessmentRepository;
    this.assessmentMapper = assessmentMapper;
    this.employeeRepository = employeeRepository;
    this.userRepository = userRepository;
    this.criteriaRepository = criteriaRepository;
  }

  public List<AssessmentResponseDto> getAllAssessments(Long employeeId, Status status) {
    List<Assessment> assessments;
    if (employeeId != null && status != null) {
      assessments = assessmentRepository.findByEmployeeIdAndStatus(employeeId, status);
    } else if (employeeId != null) {
      assessments = assessmentRepository.findByEmployeeId(employeeId);
    } else if (status != null) {
      assessments = assessmentRepository.findByStatus(status);
    } else {
      assessments = assessmentRepository.findAll();
    }
    return assessments.stream()
            .map(assessmentMapper::toDto)
            .collect(Collectors.toList());
  }

  public List<AssessmentResponseDto> getAssessmentsBySupervisor(Long supervisorId, Long employeeId, Status status, LocalDate startDate, LocalDate endDate) {
    List<Assessment> assessments;
    if (startDate != null && endDate != null) {
      if (employeeId != null && status != null) {
        assessments = assessmentRepository.findBySupervisorIdAndEmployeeIdAndStatusAndCreatedAtBetween(
                supervisorId, employeeId, status,
                startDate.atStartOfDay(),
                endDate.plusDays(1).atStartOfDay()); // tránh mất ngày cuối
      } else if (employeeId != null) {
        assessments = assessmentRepository.findBySupervisorIdAndEmployeeIdAndCreatedAtBetween(
                supervisorId, employeeId,
                startDate.atStartOfDay(),
                endDate.plusDays(1).atStartOfDay());
      } else if (status != null) {
        assessments = assessmentRepository.findBySupervisorIdAndStatusAndCreatedAtBetween(
                supervisorId, status,
                startDate.atStartOfDay(),
                endDate.plusDays(1).atStartOfDay());
      } else {
        assessments = assessmentRepository.findBySupervisorIdAndCreatedAtBetween(
                supervisorId,
                startDate.atStartOfDay(),
                endDate.plusDays(1).atStartOfDay());
      }
    } else {
      // Không có khoảng thời gian
      if (employeeId != null && status != null) {
        assessments = assessmentRepository.findBySupervisorIdAndEmployeeIdAndStatus(supervisorId, employeeId, status);
      } else if (employeeId != null) {
        assessments = assessmentRepository.findBySupervisorIdAndEmployeeId(supervisorId, employeeId);
      } else if (status != null) {
        assessments = assessmentRepository.findBySupervisorIdAndStatus(supervisorId, status);
      } else {
        assessments = assessmentRepository.findBySupervisorId(supervisorId);
      }
    }
    return assessments.stream()
            .map(assessmentMapper::toDto)
            .collect(Collectors.toList());
  }

    public List<AssessmentResponseDto> getAssessmentsByEmployee(Long employeeId, Long supervisorId, LocalDate startDate, LocalDate endDate) {
      List<Assessment> assessments;
      if (startDate != null && endDate != null) {
        // Lọc theo khoảng thời gian
        LocalDateTime startDateTime = startDate.atTime(LocalTime.MIN);
        LocalDateTime endDateTime = endDate.atTime(LocalTime.MAX);

        if (supervisorId != null) {
          assessments = assessmentRepository .findByEmployeeIdAndSupervisorIdAndCreatedAtBetween(
                  employeeId, supervisorId, startDateTime, endDateTime);
        } else {
          assessments = assessmentRepository.findByEmployeeIdAndCreatedAtBetween(
                  employeeId, startDateTime, endDateTime);
        }
      } else {
        if (supervisorId != null) {
          assessments = assessmentRepository.findByEmployeeIdAndSupervisorId(employeeId, supervisorId);
        } else {
          assessments = assessmentRepository.findByEmployeeId(employeeId);
        }
      }

      return assessments.stream()
              .map(assessmentMapper::toDto)
              .collect(Collectors.toList());
    }

  @Transactional
  public AssessmentResponseDto createAssessment(Long supervisorId, CreateAssessmentRequestDto request) {
    // Validate employee exists
    Employee employee = (Employee) userRepository.findById(request.getEmployeeId())
            .orElseThrow(() -> new RuntimeException("Employee not found with id: " + request.getEmployeeId()));

    // Get supervisor
    Supervisor supervisor = (Supervisor) userRepository.findById(supervisorId)
            .orElseThrow(() -> new RuntimeException("Supervisor not found with id: " + supervisorId));

    // Create assessment
    Assessment assessment = new Assessment();
    assessment.setSupervisor(supervisor);
    assessment.setEmployee(employee);
    assessment.setStatus(Status.InProgress);
    assessment.setCreatedAt(LocalDateTime.now());

    // Calculate total score
    double totalScore = 0;
    int totalWeight = 0;

    // Create IsBelongTo entries
    List<IsBelongTo> criteriaScores = request.getScores().stream()
            .map(scoreDto -> {
              Criteria criteria = criteriaRepository.findById(scoreDto.getCriteriaId())
                      .orElseThrow(() -> new RuntimeException("Criteria not found with id: " + scoreDto.getCriteriaId()));

              IsBelongTo isBelongTo = new IsBelongTo();
              isBelongTo.setAssessment(assessment);
              isBelongTo.setCriteria(criteria);
              isBelongTo.setScore(scoreDto.getScore());
              isBelongTo.setComment(scoreDto.getComment());

              return isBelongTo;
            })
            .collect(Collectors.toList());

    // Calculate weighted average score
    for (IsBelongTo isBelongTo : criteriaScores) {
      if (isBelongTo.getScore() != null) {
        totalScore += isBelongTo.getScore() * isBelongTo.getCriteria().getWeight();
        totalWeight += isBelongTo.getCriteria().getWeight();
      }
    }

    if (totalWeight > 0) {
      assessment.setTotalScore(totalScore / totalWeight);
    } else {
      assessment.setTotalScore(0.0);
    }

    assessment.setIsBelongTo(criteriaScores);

    // Save assessment
    Assessment savedAssessment = assessmentRepository.save(assessment);

    return assessmentMapper.toDto(savedAssessment);
  }

  @Transactional
  public AssessmentResponseDto updateAssessmentStatus(Long supervisorId, Long assessmentId, UpdateAssessmentStatusRequestDto request) {
    // Get assessment
    Assessment assessment = assessmentRepository.findById(assessmentId)
            .orElseThrow(() -> new RuntimeException("Assessment not found with id: " + assessmentId));

    // Check if supervisor owns this assessment
    if (!assessment.getSupervisor().getId().equals(supervisorId)) {
        throw new RuntimeException("You don't have permission to update this assessment");
    }

    // Parse and validate status
    Status newStatus;
    try {
        // Trim whitespace and convert string to enum
        String statusStr = request.getStatus();
        
        if (statusStr == null || statusStr.trim().isEmpty()) {
            throw new RuntimeException("Status cannot be empty");
        }
        
        statusStr = statusStr.trim();
        
        // Normalize to match enum values (case-insensitive)
        if (statusStr.equalsIgnoreCase("InProgress") || 
            statusStr.equalsIgnoreCase("in_progress") || 
            statusStr.equalsIgnoreCase("INPROGRESS")) {
            newStatus = Status.InProgress;
        } else if (statusStr.equalsIgnoreCase("Published") || 
                   statusStr.equalsIgnoreCase("PUBLISHED")) {
            newStatus = Status.Published;
          } else {
              throw new IllegalArgumentException("Invalid status: '" + statusStr + "'");
          }
      } catch (IllegalArgumentException e) {
          throw new RuntimeException("Invalid status. Must be 'InProgress' or 'Published'");
      }

      // Update status
      assessment.setStatus(newStatus);

      // Save assessment
      Assessment updatedAssessment = assessmentRepository.save(assessment);

      return assessmentMapper.toDto(updatedAssessment);
  }

  public AssessmentResponseDto getAssessmentById(Long userId, Long assessmentId) {
    // Get assessment
    Assessment assessment = assessmentRepository.findById(assessmentId)
            .orElseThrow(() -> new RuntimeException("Assessment not found with id: " + assessmentId));

    // Check permission: supervisor can view their assessments, employee can view their assigned assessments
    boolean isSupervisor = assessment.getSupervisor().getId().equals(userId);
    boolean isEmployee = assessment.getEmployee().getId().equals(userId);

    if (!isSupervisor && !isEmployee) {
      throw new RuntimeException("You don't have permission to view this assessment");
    }

    return assessmentMapper.toDto(assessment);
  }

  @Transactional
  public AssessmentResponseDto updateAssessment(Long supervisorId, Long assessmentId, CreateAssessmentRequestDto request) {
    // Get assessment
    Assessment assessment = assessmentRepository.findById(assessmentId)
            .orElseThrow(() -> new RuntimeException("Assessment not found with id: " + assessmentId));

    // Check if supervisor owns this assessment
    if (!assessment.getSupervisor().getId().equals(supervisorId)) {
      throw new RuntimeException("You don't have permission to update this assessment");
    }

    // Validate employee exists (if changing employee)
    if (request.getEmployeeId() != null && !request.getEmployeeId().equals(assessment.getEmployee().getId())) {
      Employee employee = (Employee) userRepository.findById(request.getEmployeeId())
              .orElseThrow(() -> new RuntimeException("Employee not found with id: " + request.getEmployeeId()));
      assessment.setEmployee(employee);
    }

    // Remove existing criteria scores (orphanRemoval will handle deletion)
    if (assessment.getIsBelongTo() != null) {
      assessment.getIsBelongTo().clear();
    }

    // Create new criteria scores
    List<IsBelongTo> newCriteriaScores = request.getScores().stream()
            .map(scoreDto -> {
              Criteria criteria = criteriaRepository.findById(scoreDto.getCriteriaId())
                      .orElseThrow(() -> new RuntimeException("Criteria not found with id: " + scoreDto.getCriteriaId()));

              IsBelongTo isBelongTo = new IsBelongTo();
              isBelongTo.setAssessment(assessment);
              isBelongTo.setCriteria(criteria);
              isBelongTo.setScore(scoreDto.getScore());
              isBelongTo.setComment(scoreDto.getComment());

              return isBelongTo;
            })
            .collect(Collectors.toList());

    // Add new scores to the cleared list
    assessment.getIsBelongTo().addAll(newCriteriaScores);

    // Recalculate total score
    double totalScore = 0;
    int totalWeight = 0;

    for (IsBelongTo isBelongTo : assessment.getIsBelongTo()) {
      if (isBelongTo.getScore() != null) {
        totalScore += isBelongTo.getScore() * isBelongTo.getCriteria().getWeight();
        totalWeight += isBelongTo.getCriteria().getWeight();
      }
    }

    if (totalWeight > 0) {
      assessment.setTotalScore(totalScore / totalWeight);
    } else {
      assessment.setTotalScore(0.0);
    }

    // Save assessment
    Assessment updatedAssessment = assessmentRepository.save(assessment);

    return assessmentMapper.toDto(updatedAssessment);
  }

  public DashboardSummaryResponse getEmployeeDashboard(Long employeeId, LocalDate startDate, LocalDate endDate) {
    Employee employee = employeeRepository.findById(employeeId).orElseThrow(() -> new RuntimeException("Employee not found with id: " + employeeId));
    List<DashboardResponse> monthly = getMonthlyDashboard(employeeId, startDate, endDate);

    double avg = monthly.stream()
            .mapToDouble(DashboardResponse::getAvgScore)
            .average().orElse(0);

    long total = monthly.stream()
            .mapToLong(DashboardResponse::getTotalAssessments)
            .sum();

    return new DashboardSummaryResponse(
            employeeId,
            employee.getName(),
            avg,
            total,
            monthly
    );
  }

  public List<DashboardResponse> getMonthlyDashboard(Long employeeId, LocalDate startDate, LocalDate endDate) {
    if (startDate == null) {
      startDate = LocalDate.of(1900, 1, 1);
    }
    if (endDate == null) {
      endDate = LocalDate.now();
    }

    LocalDateTime startDateTime = startDate.atTime(LocalTime.MIN);
    LocalDateTime endDateTime = endDate.atTime(LocalTime.MAX);

    List<Object[]> results = assessmentRepository.getMonthlyDashboard(employeeId, startDateTime, endDateTime);
    List<DashboardResponse> responses = new ArrayList<>();


    for (Object[] row : results) {
      DashboardResponse dto = new DashboardResponse(
              ((Number) row[0]).intValue(),   // year
              ((Number) row[1]).intValue(),   // month
              ((Number) row[2]).longValue(),  // totalAssessments
              ((Number) row[3]).doubleValue() // avgScore
      );
      responses.add(dto);
    }
    return responses;
  }

  public List<EmployeeAverageScoreDto> getEmployeesAverageScore(LocalDateTime startDate, LocalDateTime endDate, String sort) {
    // Get all assessments within the date range
    List<Assessment> assessments = assessmentRepository.findByCreatedAtBetween(startDate, endDate);
    
    // Get all employees
    List<Employee> allEmployees = employeeRepository.findAll();
    
    // Group assessments by employee
    Map<Long, List<Assessment>> assessmentsByEmployee = assessments.stream()
            .collect(Collectors.groupingBy(a -> a.getEmployee().getId()));
    
    // Calculate average score for each employee
    List<EmployeeAverageScoreDto> results = allEmployees.stream()
            .map(employee -> {
              List<Assessment> employeeAssessments = assessmentsByEmployee.get(employee.getId());
              
              // Create EmployeeDto
              EmployeeDto employeeDto = EmployeeDto.builder()
                      .id(employee.getId())
                      .name(employee.getName())
                      .email(employee.getEmail())
                      .build();
              
              EmployeeAverageScoreDto dto = new EmployeeAverageScoreDto();
              dto.setEmployee(employeeDto);
              
              if (employeeAssessments == null || employeeAssessments.isEmpty()) {
                // No assessments in this period
                dto.setAverageScore(-1.0);
                dto.setTotalAssessments(0L);
              } else {
                // Calculate average score
                double averageScore = employeeAssessments.stream()
                        .mapToDouble(Assessment::getTotalScore)
                        .average()
                        .orElse(-1.0);
                
                dto.setAverageScore(averageScore);
                dto.setTotalAssessments((long) employeeAssessments.size());
              }
              
              return dto;
            })
            .collect(Collectors.toList());
    
    // Sort the results: first by average score, then by total assessments as tiebreaker
    if ("asc".equals(sort)) {
      // Ascending order
      results.sort((a, b) -> {
        int scoreCompare = Double.compare(a.getAverageScore(), b.getAverageScore());
        if (scoreCompare != 0) {
          return scoreCompare;
        }
        // If average scores are equal, sort by total assessments
        return Long.compare(a.getTotalAssessments(), b.getTotalAssessments());
      });
    } else {
      // Descending order (default)
      results.sort((a, b) -> {
        int scoreCompare = Double.compare(b.getAverageScore(), a.getAverageScore());
        if (scoreCompare != 0) {
          return scoreCompare;
        }
        // If average scores are equal, sort by total assessments
        return Long.compare(b.getTotalAssessments(), a.getTotalAssessments());
      });
    }
    
    return results;
  }
}