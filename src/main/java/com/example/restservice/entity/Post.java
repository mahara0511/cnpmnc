package com.example.restservice.entity;

import jakarta.persistence.*;
import lombok.*;
import java.util.Arrays;
import java.util.List;


import javax.annotation.processing.Generated;

@Entity
@Table(name = "posts")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Post {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    private String profile;
    @Column(name = "description", length = 1000)
    private String desc;
    private int exp;

    @ElementCollection
    @CollectionTable(name = "post_techs", joinColumns = @JoinColumn(name = "post_id"))
    @Column(name = "tech") 
    private List<String> techs;
} 