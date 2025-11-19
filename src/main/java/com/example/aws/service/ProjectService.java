package com.example.aws.service;

import com.example.aws.domain.Project;
import com.example.aws.repository.ProjectRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.Optional;


@Service
public class ProjectService {

    private final ProjectRepository projectRepository;

    public ProjectService(ProjectRepository projectRepository) {
        this.projectRepository = projectRepository;;
    }
    /**
     * 프로젝트명과 태그 검색 조건이 모두 충족될 때만 AND 검색을 수행하고,
     * 그렇지 않으면 전체 프로젝트 목록을 반환합니다.
     */
    public Page<Project> getPosts(String name, String tags, Pageable pageable) {

        // AND 검색 조건 확인: name과 tags 둘 다 값이 있어야 true
        boolean hasCompleteSearchTerm =
                (name != null && !name.trim().isEmpty()) &&
                        (tags != null && !tags.trim().isEmpty());

        if (hasCompleteSearchTerm) {
            // 두 검색어가 모두 있을 경우: AND 조건으로 검색을 수행
            String searchName = name.trim();
            String searchTags = tags.trim();

            // 변경된 AND 검색 메서드 호출
            return projectRepository.findByProjectNameContainingIgnoreCaseAndTagsContainingIgnoreCase(
                    searchName,
                    searchTags,
                    pageable
            );
        } else {
            // 두 검색 조건 중 하나라도 없거나 비어있는 경우: 전체 프로젝트 조회 (메인 페이지 기본 로딩)
            return projectRepository.findAll(pageable);
        }
    }


/*    // 전체 프로젝트 조회 (페이지 처리)
    public Page<Project> getPosts(Pageable pageable) {
        return projectRepository.findAll(pageable);
    }*/

    // 단일 프로젝트 조회
    public Optional<Project> getPost(Long id) {
        return projectRepository.findById(id);
    }

    // 프로젝트 생성
    public Project createPost(Project project) {
        return projectRepository.save(project);
    }
}