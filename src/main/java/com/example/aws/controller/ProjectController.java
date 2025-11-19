package com.example.aws.controller;

import com.example.aws.domain.Project;
import com.example.aws.service.ProjectService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@RestController
@RequestMapping("api/mainpage")
public class ProjectController {

    @Autowired
    private ProjectService projectService;

    // 프로젝트 목록 조회 (페이지네이션 적용: 기본 페이지 번호=0, 페이지 사이즈=10)
    @GetMapping
    public ResponseEntity<Page<Project>> listPosts(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false) String name, // 파라미터가 없으면 null
            @RequestParam(required = false) String tags) { // 파라미터가 없으면 null

        Pageable pageable = PageRequest.of(page, size);
        Page<Project> posts = projectService.getPosts(name, tags, pageable);
        return ResponseEntity.ok(posts);
    }

    // 프로젝트 단일 조회
    @GetMapping("/{id}")
    public ResponseEntity<Project> getPost(@PathVariable Long id) {
        Optional<Project> postOpt = projectService.getPost(id);
        return postOpt.map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    // 프로젝트 생성
    @PostMapping
    public ResponseEntity<Project> createPost(@RequestBody Project post) {
        Project createdPost = projectService.createPost(post);
        return ResponseEntity.ok(createdPost);
    }


}
