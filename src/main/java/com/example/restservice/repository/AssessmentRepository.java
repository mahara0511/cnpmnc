package com.example.restservice.repository;

import com.example.restservice.entity.Assessment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface AssessmentRepository extends JpaRepository<Assessment, Long> {
    
    // For SUPERVISOR
    List<Assessment> findBySupervisorId(Long supervisorId);
    List<Assessment> findBySupervisorIdAndEmployeeId(Long supervisorId, Long employeeId);
    List<Assessment> findBySupervisorIdAndStatus(Long supervisorId, String status);
    List<Assessment> findBySupervisorIdAndEmployeeIdAndStatus(Long supervisorId, Long employeeId, String status);
    
    // For EMPLOYEE
    List<Assessment> findByEmployeeIdAndSupervisorId(Long employeeId, Long supervisorId);
    List<Assessment> findByEmployeeId(Long employeeId);
    List<Assessment> findByStatus(String status);
    
    List<Assessment> findByEmployeeIdAndStatus(Long employeeId, String status);
}   