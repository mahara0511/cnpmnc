package com.example.restservice.mapper;

import com.example.restservice.entity.Employee;
import com.example.restservice.response.UserResponse;
import org.springframework.stereotype.Component;

@Component
public class UserMapper {
    public UserResponse toDTO(Employee employee) {
        if (employee == null) return null;
        UserResponse dto = new UserResponse();
        dto.setId(employee.getId());
        dto.setName(employee.getName());
        dto.setEmail(employee.getEmail());
        return dto;
    }
}
