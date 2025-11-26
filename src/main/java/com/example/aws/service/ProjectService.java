package com.example.aws.service;

import com.example.aws.domain.Project;
import com.example.aws.repository.ProjectRepository;
import com.opencsv.CSVReader;
import com.opencsv.exceptions.CsvException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import software.amazon.awssdk.core.ResponseInputStream;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.GetObjectRequest;
import software.amazon.awssdk.services.s3.model.GetObjectResponse;

import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
public class ProjectService {

    private final ProjectRepository projectRepository;
    private final S3Client s3Client; // S3에서 파일 가져오기 위해 필요

    @Value("${aws-project-s3-gcf}")
    private String bucketName;

    @Transactional
    public void importFromS3(String s3FileName) throws IOException, CsvException {
        log.info("S3에서 파일 다운로드 및 DB 적재 시작: {}", s3FileName);

        // 1. S3에서 파일 스트림(빨대) 꽂기
        GetObjectRequest getObjectRequest = GetObjectRequest.builder()
                .bucket(bucketName)
                .key(s3FileName) // 가져올 파일 이름
                .build();

        ResponseInputStream<GetObjectResponse> s3Object = s3Client.getObject(getObjectRequest);

        // 2. CSV 내용 읽어서 DB에 저장하기
        try (CSVReader csvReader = new CSVReader(new InputStreamReader(s3Object, StandardCharsets.UTF_8))) {
            List<String[]> records = csvReader.readAll();
            List<Project> newProjects = new ArrayList<>();

            // 첫 번째 줄(제목) 건너뛰기
            for (int i = 1; i < records.size(); i++) {
                String[] record = records.get(i);

                // 컬럼 순서: project_name, project_summary, source, url, tags
                if (record.length >= 5) {
                    Project project = new Project(
                            record[0], // projectName
                            record[1], // projectSummary
                            record[2], // source
                            record[3], // url
                            record[4]  // tags
                    );
                    newProjects.add(project);
                }
            }

            projectRepository.saveAll(newProjects);
            log.info("성공! 총 {} 개의 프로젝트가 DB에 저장되었습니다.", newProjects.size());
        }
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