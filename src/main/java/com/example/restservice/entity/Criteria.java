package com.example.restservice.entity;

import com.example.restservice.common.enums.Category;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Entity
@Data
@Table(name ="criteria")
@AllArgsConstructor
@NoArgsConstructor
public class Criteria {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @Column(name = "name")
    private String name;

    @Column(name = "description",columnDefinition = "TEXT")
    private String description;

    @Column(name = "weight")
    private double weight;

    @Column(name="Category")
    @Enumerated(EnumType.STRING)
    private Category category;

    @OneToMany(mappedBy = "criteria",cascade = CascadeType.ALL)
    private List<IsBelongTo> isBelongTo;

}
