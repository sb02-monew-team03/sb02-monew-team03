package com.team03.monew.storage;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.team03.monew.dto.newsArticle.mapper.NewsArticleBackupMapper;
import com.team03.monew.dto.newsArticle.response.NewsArticleBackupDto;
import com.team03.monew.entity.NewsArticle;
import java.time.LocalDate;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;

@RequiredArgsConstructor
@Component
public class S3BackupStorage implements BackupStorage {

    private final S3Client s3Client; // v2 SDK
    private final ObjectMapper objectMapper;

    @Value("${aws.s3.bucket}")
    private String bucket;

    @Override
    public void backupArticles(List<NewsArticle> articles, LocalDate date) {
        try {
            // DTO 변환
            List<NewsArticleBackupDto> dtos = articles.stream()
                .map(NewsArticleBackupMapper::toDto)
                .toList();

            // S3 경로 지정
            String key = String.format("backup/%s/news_backup.json", date);
            // JSON 직렬화
            String json = objectMapper.writeValueAsString(dtos);

            // 업로드 요청 생성
            PutObjectRequest putRequest = PutObjectRequest.builder()
                .bucket(bucket)
                .key(key)
                .contentType("application/json")
                .build();

            // 업로드
            s3Client.putObject(putRequest, RequestBody.fromString(json));
            System.out.println("뉴스 기사 백업 성공: {key : " + key +"}");
        } catch (Exception e) {
            System.out.println("뉴스 기사 백업 실패: {" + e.getMessage() + "}");
        }
    }
}
