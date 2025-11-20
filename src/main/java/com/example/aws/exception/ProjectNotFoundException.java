package com.example.aws.exception;

public class ProjectNotFoundException extends RuntimeException {
    public ProjectNotFoundException(String message) {
        super(message);
    }

    public ProjectNotFoundException(Long id) {
        super("프로젝트를 찾을 수 없습니다. ID: " + id);
    }
}


