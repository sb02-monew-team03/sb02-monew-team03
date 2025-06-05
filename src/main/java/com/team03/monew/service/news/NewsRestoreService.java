package com.team03.monew.service.news;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.team03.monew.dto.newsArticle.response.ArticleRestoreResultDto;
import com.team03.monew.dto.newsArticle.response.NewsArticleBackupDto;
import com.team03.monew.entity.Interest;
import com.team03.monew.entity.NewsArticle;
import com.team03.monew.repository.InterestRepository;
import com.team03.monew.repository.NewsArticleRepository;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import software.amazon.awssdk.core.ResponseInputStream;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.GetObjectRequest;
import software.amazon.awssdk.services.s3.model.GetObjectResponse;

@RequiredArgsConstructor
@Service
public class NewsRestoreService {

    private final S3Client s3Client;
    private final ObjectMapper objectMapper;
    private final NewsArticleRepository newsArticleRepository;
    private final InterestRepository interestRepository;

    @Value("${aws.s3.bucket}")
    private String bucket;

    // 유실된 데이터 복구
    @Transactional
    public List<ArticleRestoreResultDto> restore(LocalDate from, LocalDate to) {
        List<ArticleRestoreResultDto> results = new ArrayList<>();

        for (LocalDate date = from; !date.isAfter(to); date = date.plusDays(1)) {
            String key = String.format("backup/%s/news_backup.json", date);
            try {
                // S3에서 백업 파일 다운로드
                ResponseInputStream<GetObjectResponse> inputStream = s3Client.getObject(
                    GetObjectRequest.builder().bucket(bucket).key(key).build()
                );

                // JSON → List<Dto> 역직렬화
                List<NewsArticleBackupDto> dtos = objectMapper.readValue(
                    inputStream,
                    new TypeReference<>() {}
                );

                // 복구할 기사 생성. Dto → Entity로 변환 후 저장
                List<NewsArticle> articles = dtos.stream()
                    // 중복 제거
                    .filter(dto -> !newsArticleRepository.existsByOriginalLink(dto.originalLink()))
                    .map(dto -> {
                        Optional<Interest> optionalInterest = interestRepository.findById(dto.interestId());

                        if (optionalInterest.isEmpty()) {
                            System.out.println("⏭ 관심사 없음, 복구 제외됨: title=" + dto.title() + ", interestId=" + dto.interestId());
                            return Optional.<NewsArticle>empty(); // 관심사 없으면 제외
                        }

                        Interest interest = optionalInterest.get();
                        NewsArticle article = NewsArticle.builder()
                            .source(dto.source())
                            .originalLink(dto.originalLink())
                            .title(dto.title())
                            .summary(dto.summary())
                            .date(dto.date())
                            .interest(interest)
                            .viewCount(0)
                            .deleted(false)
                            .build();

                        return Optional.of(article);
                    })
                    .flatMap(Optional::stream) // Optional.empty()는 무시됨
                    .toList();

                // 저장
                List<NewsArticle> saved = newsArticleRepository.saveAll(articles);

                // 복구 결과 생성
                List<UUID> ids = saved.stream().map(NewsArticle::getId).toList();
                results.add(new ArticleRestoreResultDto(
                    LocalDateTime.now(),  // 복구 수행 시각
                    ids,
                    ids.size()
                ));
                System.out.println("복구 성공");
            } catch (Exception e) {
                System.out.println("복구 실패: " + e.getClass().getSimpleName() + " - " + e.getMessage());
                e.printStackTrace();
            }
        }

        return results;
    }
}