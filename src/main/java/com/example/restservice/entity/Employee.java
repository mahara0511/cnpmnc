package com.example.restservice.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Entity
@Table(name = "employee")
public class Employee extends User {

    @OneToMany(mappedBy = "employee", cascade = CascadeType.ALL)
    private List<Assessment> assessment;
}
