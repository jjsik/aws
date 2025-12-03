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

    // 프로젝트 목록 조회
    @GetMapping
    public ResponseEntity<Page<Project>> listPosts(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false) String name,
            @RequestParam(required = false) String tags) {

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

    @PostMapping("/import-s3")
    public ResponseEntity<String> importFromS3(@RequestParam("filename") String filename) {
        try {
            projectService.importFromS3(filename);
            return ResponseEntity.ok("S3 파일(" + filename + ")을 DB에 성공적으로 저장했습니다!");
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.internalServerError().body("오류 발생: " + e.getMessage());
        }
    }


}
