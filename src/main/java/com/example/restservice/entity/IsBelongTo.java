package com.example.restservice.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.checkerframework.checker.units.qual.A;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "isbelongto")
public class IsBelongTo {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private long id;

    @Column(name = "score")
    private Long score;

    @Column(name = "comment",columnDefinition = "TEXT")
    private String comment;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "assessmentId",referencedColumnName = "id")
    private Assessment assessment;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "criteriaId",referencedColumnName ="id")
    private Criteria criteria;
}
