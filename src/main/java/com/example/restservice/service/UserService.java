package com.example.restservice.service;

import com.example.restservice.dto.CreateEmployeeDTO;
import com.example.restservice.entity.Employee;
import com.example.restservice.mapper.UserMapper;
import com.example.restservice.entity.Supervisor;
import com.example.restservice.repository.EmployeeRepository;
import com.example.restservice.repository.UserRepository;
import com.example.restservice.entity.User;
import com.example.restservice.response.UserMeResponse;
import com.example.restservice.response.UserResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import com.example.restservice.common.enums.AuthProvider;
import com.example.restservice.util.JWTUtil;

import java.util.List;
import java.util.Optional;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

@Service
public class UserService {
    private final UserRepository userRepository;
    private final JWTUtil jwtUtil;

    @Autowired
    private EmployeeRepository employeeRepository;
    @Autowired
    private UserMapper userMapper;

    public UserService(UserRepository userRepository, JWTUtil jwtUtil) {
        this.userRepository = userRepository;
        this.jwtUtil = jwtUtil;
    }

    private String hashPassword(String rawPassword) {
        return new BCryptPasswordEncoder().encode(rawPassword);
    }

    public boolean existsByEmail(String email) {
        return userRepository.existsByEmail(email);
    }

    public Optional<User> findByEmail(String email) {
        return userRepository.findByEmail(email);
    }

    public User save(String email, String password, String name) {
        Employee employee = new Employee();
        employee.setName(name);
        employee.setEmail(email);
        employee.setPassword(this.hashPassword(password));
        return employeeRepository.save(employee);
    }
    public Page<UserResponse> findEmploye(String query, Pageable pageable){
        Page<Employee> employees;

        if (query != null && !query.trim().isEmpty()) {
            employees = employeeRepository.findByNameContainingIgnoreCase(query, pageable);
        } else {
            employees = employeeRepository.findAll(pageable);
        }

        return employees.map(userMapper::toDTO);
    }

    public boolean existsByJwt(String jwt) {
        return jwtUtil.validateToken(jwt);
    }

    public Optional<UserMeResponse> getCurrentUserByEmail(String email) {
        return userRepository.findByEmail(email).map(this::toUserMeResponse);
    }

    private UserMeResponse toUserMeResponse(User user) {
        String role;
        if (user instanceof Supervisor) {
            role = "SUPERVISOR";
        } else if (user instanceof Employee) {
            role = "EMPLOYEE";
        } else {
            role = "USER";
        }
        return new UserMeResponse(user.getId(), user.getName(), user.getEmail(), role);
    }

    public UserResponse createEmployee(CreateEmployeeDTO req) {
        if(existsByEmail(req.getEmail())) throw new RuntimeException("Email already exists");
        User employee = save(req.getEmail(), req.getPassword(), req.getName());
        return userMapper.toDTO(employee);
    }
}

