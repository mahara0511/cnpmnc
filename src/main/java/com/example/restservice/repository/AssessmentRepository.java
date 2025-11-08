package com.example.restservice.repository;

import com.example.restservice.entity.Assessment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import com.example.restservice.common.enums.Status;

@Repository
public interface AssessmentRepository extends JpaRepository<Assessment, Long> {
    
    // For SUPERVISOR
    List<Assessment> findBySupervisorId(Long supervisorId);
    List<Assessment> findBySupervisorIdAndEmployeeId(Long supervisorId, Long employeeId);
    List<Assessment> findBySupervisorIdAndStatus(Long supervisorId, Status status);
    List<Assessment> findBySupervisorIdAndEmployeeIdAndStatus(Long supervisorId, Long employeeId, Status status);

    // For EMPLOYEE
    List<Assessment> findByEmployeeIdAndSupervisorId(Long employeeId, Long supervisorId);
    List<Assessment> findByEmployeeId(Long employeeId);
    List<Assessment> findByStatus(Status status);
    @Query("""
        SELECT YEAR(a.createdAt) AS year,
               MONTH(a.createdAt) AS month,
               COUNT(a.id) AS totalAssessments,
               AVG(a.totalScore) AS avgScore
        FROM Assessment a
        WHERE a.employee.id = :employeeId
            AND a.status = com.example.restservice.common.enums.Status.Completed
        GROUP BY YEAR(a.createdAt), MONTH(a.createdAt)
        ORDER BY YEAR(a.createdAt), MONTH(a.createdAt)
    """)
    List<Object[]> getMonthlyDashboard(@Param("employeeId") Long employeeId);

    List<Assessment> findByEmployeeIdAndStatus(Long employeeId, Status status);
    
    // Find assessments by employee and date range
    @Query("SELECT a FROM Assessment a WHERE a.employee.id = :employeeId AND a.createdAt BETWEEN :startDate AND :endDate")
    List<Assessment> findByEmployeeIdAndCreatedAtBetween(
            @Param("employeeId") Long employeeId,
            @Param("startDate") LocalDateTime startDate,
            @Param("endDate") LocalDateTime endDate
    );
    
    // Find all assessments within date range for all employees
    @Query("SELECT a FROM Assessment a WHERE a.createdAt BETWEEN :startDate AND :endDate")
    List<Assessment> findByCreatedAtBetween(
            @Param("startDate") LocalDateTime startDate,
            @Param("endDate") LocalDateTime endDate
    );
}   