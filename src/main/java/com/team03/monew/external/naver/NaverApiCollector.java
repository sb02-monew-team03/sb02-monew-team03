package com.team03.monew.external.naver;

import com.team03.monew.dto.newsArticle.request.NewsArticleRequestDto;
import com.team03.monew.entity.Interest;
import com.team03.monew.repository.InterestRepository;
import com.team03.monew.service.NewsArticleService;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

@Component
@RequiredArgsConstructor
public class NaverApiCollector {
    @Value("${naver.api.client-id}")
    private String clientId;

    @Value("${naver.api.client-secret}")
    private String clientSecret;

    private final NewsArticleService newsArticleService;
    private final InterestRepository interestRepository;

    public void collect() {
        List<Interest> interests = interestRepository.findAll();

        for (Interest interest : interests) {
            for (String keyword : interest.getKeywords()) {
                try {
                    // 키워드를 인코딩하여 api 호출
                    String encoded = URLEncoder.encode(keyword, StandardCharsets.UTF_8);
                    String url = "https://openapi.naver.com/v1/search/news.json?query=" + encoded + "&display=10&sort=date";

                    // 인증 헤더 추가
                    HttpHeaders headers = new HttpHeaders();
                    headers.set("X-Naver-Client-Id", clientId);
                    headers.set("X-Naver-Client-Secret", clientSecret);

                    // api 호출 및 응답 매핑
                    HttpEntity<Void> request = new HttpEntity<>(headers);
                    ResponseEntity<NaverResponseDto> response = new RestTemplate().exchange(
                        url, HttpMethod.GET, request, NaverResponseDto.class);

                    // 응답에서 각 기사 리스트 추출
                    List<NaverNewsItem> items = response.getBody().getItems();
                    for (NaverNewsItem item : items) {
                        // DTO로 변환
                        NewsArticleRequestDto dto = item.toDto(interest.getId());
                        newsArticleService.saveIfNotExists(dto);
                    }

                } catch (Exception e) {
                    System.err.println("네이버 뉴스 수집 실패: " + keyword);
                    e.printStackTrace();
                }
            }
        }
    }
}
