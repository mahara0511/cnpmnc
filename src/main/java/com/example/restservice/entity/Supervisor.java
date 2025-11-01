package com.example.restservice.entity;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Entity
@Table (name ="supervisor")
public class Supervisor extends User {
    @OneToMany(mappedBy = "supervisor", cascade = CascadeType.ALL)
    private List<Assessment> assessments;

}
