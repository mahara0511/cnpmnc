package com.example.restservice.repository;

import com.example.restservice.entity.Employee;
import com.example.restservice.response.UserResponse;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.UUID;

public interface EmployeeRepository  extends JpaRepository<Employee, Long> {
    @Query("SELECT new com.example.restservice.response.UserResponse(e.name, e.email, e.id) FROM Employee e")
    List<UserResponse> findAllEmployees();
}
