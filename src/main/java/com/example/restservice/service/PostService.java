package com.example.restservice.service;

import com.example.restservice.dto.PostDto;
import com.example.restservice.entity.Post;
import com.example.restservice.repository.PostRepository;
import org.springframework.stereotype.Service;
import java.util.Arrays;
import java.util.List;

@Service
public class PostService {
    private final PostRepository postRepository;
    public PostService(PostRepository postRepository) {
        this.postRepository = postRepository;
    }

    public List<Post> getAllPosts() {
        return postRepository.findAll();
    }

    public List<Post> getPostsByTech(String tech) {
        return postRepository.findByTechsContaining(tech);
    }

    public PostDto createPost(PostDto post) {
        Post newPost = new Post(
            null,
            post.getProfile(),
            post.getDesc(),
            post.getExp(),
            Arrays.asList(post.getTechs())
        );
        postRepository.save(newPost);
        return post;
    }
}