package com.example.restservice.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Entity
@Data
@Table(name = "assessment")
@NoArgsConstructor
@AllArgsConstructor
public class Assessment {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @Column(name = "totalScore")
    private double totalScore;

    @OneToMany(mappedBy = "assessment", cascade = CascadeType.ALL)
    private List<IsBelongTo> isBelongTo;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "supervisorId",referencedColumnName = "id")
    private Supervisor supervisor;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "employee_id", unique = true)
    private Employee employee;
}
