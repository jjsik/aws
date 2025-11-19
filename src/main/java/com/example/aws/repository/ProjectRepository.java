package com.example.aws.repository;

import com.example.aws.domain.Project;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ProjectRepository extends JpaRepository<Project, Long> {

    Page<Project> findByProjectNameContainingIgnoreCaseAndTagsContainingIgnoreCase(
            String projectName,
            String tags,
            Pageable pageable
    );

}