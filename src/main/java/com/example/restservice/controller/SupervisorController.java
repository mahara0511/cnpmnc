package com.example.restservice.controller;

import com.example.restservice.entity.Supervisor;
import com.example.restservice.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/supervisor")
public class SupervisorController {
    @Autowired
    private UserService userService;
    @GetMapping("/getemployeeist")
    public ResponseEntity<?> getEmployeeList() {
        try {
            Object message =userService.findEmploye();
            return ResponseEntity.ok(message);
        }catch (Exception e){
            return ResponseEntity.status(500).body("Internal server error");
        }
    }
}
