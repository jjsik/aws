package com.example.aws.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Data;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;


@Entity
@Table(name = "crawling_data") // my_sql 테이블명으로 지정했음
@Data
public class Project {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id; // primary_key

    @Column(nullable = false)
    private String projectName;

    @Column(nullable = false, length = 5000)
    private String projectSummary;

    private String source;
    private String url;
    private String tags;


    // 기본 생성자
    public Project() {}

    // 생성자
    public Project(String projectName, String projectSummary, String source
    , String url, String tags)
    {
        this.projectName = projectName;
        this.projectSummary = projectSummary;
        this.source = source;
        this.url = url;
        this.tags = tags;
    }
}