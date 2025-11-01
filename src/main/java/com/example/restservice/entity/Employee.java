package com.example.restservice.entity;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "employee")
public class Employee extends User {

    @OneToOne(mappedBy = "employee", cascade = CascadeType.ALL)
    private Assessment assessment;
}
