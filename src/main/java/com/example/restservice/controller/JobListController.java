package com.example.restservice.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.List;

import com.example.restservice.dto.PostDto;
import com.example.restservice.entity.Post;
import com.example.restservice.repository.PostRepository;
import com.example.restservice.service.PostService;


@RestController
@ApiResponse(responseCode = "200", description = "Thành công")
@ApiResponse(responseCode = "500", description = "Lỗi máy chủ nội bộ")
@Tag(name = "Job List Controller", description = "Controller cho danh sách công việc")  
public class JobListController {
    private final PostService postService;

    public JobListController(PostService postService) {
        this.postService = postService;
    }

    @Operation(summary = "Lấy tất cả bài đăng công việc", description = "Trả về danh sách tất cả các bài đăng công việc")
    @GetMapping("/posts")
    public List<Post> getAllPosts() {
        return postService.getAllPosts();
    } 

    @Operation(summary = "Tạo bài đăng công việc mới", description = "Tạo một bài đăng công việc mới với thông tin được cung cấp")
    @PostMapping("/posts")
    public PostDto createPost(@RequestBody PostDto postDto) {

        return postService.createPost(postDto);
    }
}
