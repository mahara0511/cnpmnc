package com.example.restservice.controller;

import com.example.restservice.entity.Supervisor;
import com.example.restservice.response.UserResponse;
import com.example.restservice.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/supervisor")
public class SupervisorController {
    @Autowired
    private UserService userService;
    @GetMapping("/employee")
    public ResponseEntity<?> getEmployeeList(
            @RequestParam(defaultValue = "") String query,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ) {
        try {
            Pageable pageable = PageRequest.of(page, size, Sort.by("name").descending());
            Page<UserResponse> employees = userService.findEmploye(query, pageable);
            Map<String, Object> data = new HashMap<>();
            data.put("content", employees.getContent());
            data.put("first", employees.isFirst());
            data.put("last", employees.isLast());
            data.put("pageNumber", employees.getNumber());
            data.put("numberOfElements", employees.getTotalElements());
            data.put("size", employees.getSize());
            data.put("totalElements", employees.getTotalElements());
            data.put("totalPages", employees.getTotalPages());

            Map<String, Object> response = new HashMap<>();
            response.put("message", "Success");
            response.put("status", 200);
            response.put("data", data);

            return ResponseEntity.ok(response);
        }catch (Exception e){
            return ResponseEntity.status(500).body("Internal server error");
        }
    }
}
