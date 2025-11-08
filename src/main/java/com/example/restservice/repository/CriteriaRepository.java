package com.example.restservice.repository;

import com.example.restservice.entity.Criteria;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.UUID;
import java.util.List;

@Repository
public interface CriteriaRepository extends JpaRepository<Criteria, Long> {
    List<Criteria> findByNameContainingIgnoreCase(String name);
    
    Page<Criteria> findByNameContainingIgnoreCaseOrDescriptionContainingIgnoreCase(
        String name, String description, Pageable pageable
    );
}
