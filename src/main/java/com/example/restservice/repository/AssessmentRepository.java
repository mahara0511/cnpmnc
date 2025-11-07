package com.example.restservice.repository;

import com.example.restservice.common.enums.AssessmentStatus;
import com.example.restservice.entity.Assessment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface AssessmentRepository extends JpaRepository<Assessment, Long> {
    
    // For EMPLOYEE: get all Published assessments (optionally by supervisor ID)
    @Query("SELECT a FROM Assessment a " +
           "LEFT JOIN FETCH a.supervisor " +
           "LEFT JOIN FETCH a.employee " +
           "LEFT JOIN FETCH a.isBelongTo ib " +
           "LEFT JOIN FETCH ib.criteria " +
           "WHERE a.status = 'Published' " +
           "AND (:supervisorId IS NULL OR a.supervisor.id = :supervisorId)")
    List<Assessment> findBySupervisorIdAndPublished(@Param("supervisorId") Long supervisorId);
    
    // For SUPERVISOR: get assessments (optionally by employee ID and status)
    @Query("SELECT a FROM Assessment a " +
           "LEFT JOIN FETCH a.supervisor " +
           "LEFT JOIN FETCH a.employee " +
           "LEFT JOIN FETCH a.isBelongTo ib " +
           "LEFT JOIN FETCH ib.criteria " +
           "WHERE (:employeeId IS NULL OR a.employee.id = :employeeId) " +
           "AND (:status IS NULL OR a.status = :status)")
    List<Assessment> findByEmployeeIdAndStatus(@Param("employeeId") Long employeeId, 
                                               @Param("status") AssessmentStatus status);
}
