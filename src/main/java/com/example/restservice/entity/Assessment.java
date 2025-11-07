package com.example.restservice.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.Instant;
import java.util.List;

import com.example.restservice.common.enums.AssessmentStatus;

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

    @Column(name = "status")
    @Enumerated(EnumType.STRING)
    private AssessmentStatus status;

    @OneToMany(mappedBy = "assessment", cascade = CascadeType.ALL)
    private List<IsBelongTo> isBelongTo;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "supervisorId",referencedColumnName = "id")
    private Supervisor supervisor;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "employee_id", unique = true)
    private Employee employee;

    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    private Instant createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at")
    private Instant updatedAt;
}
