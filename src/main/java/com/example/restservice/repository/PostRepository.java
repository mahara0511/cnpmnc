package com.example.restservice.repository;

import com.example.restservice.entity.Post;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.UUID;

public interface PostRepository extends JpaRepository<Post, UUID>{}