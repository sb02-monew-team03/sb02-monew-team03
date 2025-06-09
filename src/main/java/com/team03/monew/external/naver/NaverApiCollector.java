package com.team03.monew.external.naver;

import com.team03.monew.dto.newsArticle.request.NewsArticleRequestDto;
import com.team03.monew.dto.newsArticle.response.NaverResponseDto;
import com.team03.monew.entity.Interest;
import com.team03.monew.repository.InterestRepository;
import com.team03.monew.repository.NewsArticleRepository;
import com.team03.monew.service.news.NewsArticleService;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
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
    private final NewsArticleRepository newsArticleRepository;

    public void collect() {
        List<Interest> interests = interestRepository.findAllWithKeywords();
        Map<String, NaverNewsItem> itemMap = new HashMap<>(); // 기사 링크 기준으로 Item 보관, key: 뉴스링크, value: NaverNewsItem
        Map<String, Set<UUID>> articleInterestMap = new HashMap<>(); // 기사 link -> 관심사 ID 누적, key: 뉴스링크, value: 관심사 ID들

        for (Interest interest : interests) {
            for (String keyword : interest.getKeywords()) {
                try {
                    // 키워드를 인코딩하여 api 호출
                    String encoded = URLEncoder.encode(keyword, StandardCharsets.UTF_8);
                    String url = "https://openapi.naver.com/v1/search/news.json?query=" + encoded
                        + "&display=10&sort=date";

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
                        String link = item.getLink();

                        // 기사 저장 (같은 기사면 덮어씀)
                        itemMap.putIfAbsent(link, item);

                        // 관심사 ID 누적
                        articleInterestMap
                            .computeIfAbsent(link, k -> new HashSet<>())
                            .add(interest.getId());
                    }
                } catch (Exception e) {
                    System.err.println("네이버 뉴스 수집 실패: " + keyword);
                    e.printStackTrace();
                }
            }
        }

        // 누적된 기사들을 기준으로 저장 시도
        for (Map.Entry<String, Set<UUID>> entry : articleInterestMap.entrySet()) {
            String link = entry.getKey();
            Set<UUID> interestIds = entry.getValue();

            if (!newsArticleRepository.existsByOriginalLink(link)) {
                NaverNewsItem item = itemMap.get(link);
                if (item == null)
                    continue; // 예외 케이스 방지

                NewsArticleRequestDto dto = item.toDto(new ArrayList<>(interestIds));
                newsArticleService.saveIfNotExists(dto);
            }
        }
    }
}
