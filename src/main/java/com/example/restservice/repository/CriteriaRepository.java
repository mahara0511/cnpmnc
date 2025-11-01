package com.example.restservice.repository;

import com.example.restservice.entity.Criteria;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface CriteriaRepository extends JpaRepository<Criteria, Long> {
}
