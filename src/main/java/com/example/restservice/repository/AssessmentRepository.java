package com.example.restservice.repository;

import com.example.restservice.entity.Assessment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Locale;

import com.example.restservice.common.enums.Status;

@Repository
public interface AssessmentRepository extends JpaRepository<Assessment, Long> {
    
    // For SUPERVISOR
    List<Assessment> findBySupervisorId(Long supervisorId);
    List<Assessment> findBySupervisorIdAndEmployeeId(Long supervisorId, Long employeeId);
    List<Assessment> findBySupervisorIdAndStatus(Long supervisorId, Status status);
    List<Assessment> findBySupervisorIdAndEmployeeIdAndStatus(Long supervisorId, Long employeeId, Status status);
    List<Assessment> findBySupervisorIdAndCreatedAtBetween(Long supervisorId, LocalDateTime start, LocalDateTime end);
    List<Assessment> findBySupervisorIdAndEmployeeIdAndCreatedAtBetween(Long supervisorId, Long employeeId, LocalDateTime start, LocalDateTime end);
    List<Assessment> findBySupervisorIdAndStatusAndCreatedAtBetween(Long supervisorId, Status status, LocalDateTime start, LocalDateTime end);
    List<Assessment> findBySupervisorIdAndEmployeeIdAndStatusAndCreatedAtBetween(Long supervisorId, Long employeeId, Status status, LocalDateTime start, LocalDateTime end);

    // For EMPLOYEE
    List<Assessment> findByEmployeeIdAndSupervisorId(Long employeeId, Long supervisorId);
    List<Assessment> findByEmployeeId(Long employeeId);
    List<Assessment> findByStatus(Status status);
    List<Assessment> findByEmployeeIdAndCreatedAtBetween(Long employeeId, LocalDateTime startDate, LocalDateTime endDate);
    List<Assessment> findByEmployeeIdAndSupervisorIdAndCreatedAtBetween(Long employeeId, Long supervisorId, LocalDateTime startDate, LocalDateTime endDate);
    @Query("""
        SELECT YEAR(a.createdAt) AS year,
               MONTH(a.createdAt) AS month,
               COUNT(a.id) AS totalAssessments,
               AVG(a.totalScore) AS avgScore
        FROM Assessment a
        WHERE a.employee.id = :employeeId
            AND a.status = com.example.restservice.common.enums.Status.Published
            AND a.createdAt BETWEEN :start AND :end
        GROUP BY YEAR(a.createdAt), MONTH(a.createdAt)
        ORDER BY YEAR(a.createdAt), MONTH(a.createdAt)
    """)
    List<Object[]> getMonthlyDashboard(@Param("employeeId") Long employeeId,
                                       @Param("start") LocalDateTime start,
                                       @Param("end") LocalDateTime end);

    List<Assessment> findByEmployeeIdAndStatus(Long employeeId, Status status);
    
    // Find assessments by employee and date range
//    @Query("SELECT a FROM Assessment a WHERE a.employee.id = :employeeId AND a.createdAt BETWEEN :startDate AND :endDate")
//    List<Assessment> findByEmployeeIdAndCreatedAtBetween(
//            @Param("employeeId") Long employeeId,
//            @Param("startDate") LocalDateTime startDate,
//            @Param("endDate") LocalDateTime endDate
//    );
    
    // Find all assessments within date range for all employees
    @Query("SELECT a FROM Assessment a WHERE a.createdAt BETWEEN :startDate AND :endDate")
    List<Assessment> findByCreatedAtBetween(
            @Param("startDate") LocalDateTime startDate,
            @Param("endDate") LocalDateTime endDate
    );
}   