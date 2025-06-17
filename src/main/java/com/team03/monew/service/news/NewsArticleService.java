package com.team03.monew.service.news;

import com.team03.monew.dto.newsArticle.mapper.ArticleViewMapper;
import com.team03.monew.dto.newsArticle.mapper.NewsArticleMapper;
import com.team03.monew.dto.newsArticle.request.NewsArticleRequestDto;
import com.team03.monew.dto.newsArticle.response.ArticleDto;
import com.team03.monew.dto.newsArticle.response.ArticleViewDto;
import com.team03.monew.dto.newsArticle.response.CursorPageResponseArticleDto;
import com.team03.monew.dto.newsArticle.response.NaverResponseDto;
import com.team03.monew.entity.ArticleView;
import com.team03.monew.entity.Interest;
import com.team03.monew.entity.NewsArticle;
import com.team03.monew.entity.User;
import com.team03.monew.exception.CustomException;
import com.team03.monew.exception.ErrorCode;
import com.team03.monew.exception.ErrorDetail;
import com.team03.monew.exception.ExceptionType;
import com.team03.monew.external.naver.NaverNewsItem;
import com.team03.monew.repository.ArticleViewRepository;
import com.team03.monew.repository.InterestRepository;
import com.team03.monew.repository.NewsArticleRepository;
import com.team03.monew.repository.UserRepository;
import com.team03.monew.service.InterestService;
import com.team03.monew.service.activity.ActivityDocumentUpdater;
import com.team03.monew.util.RSSUtils;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Base64;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;

@Slf4j
@Service
@RequiredArgsConstructor
public class NewsArticleService {

    private final NewsArticleRepository newsArticleRepository;
    private final ArticleViewRepository articleViewRepository;
    private final InterestRepository interestRepository;
    private final UserRepository userRepository;
    private final RSSUtils rssUtils;
    private final InterestService interestService;
    private final ActivityDocumentUpdater activityDocumentUpdater;

    @Value("${aws.s3.bucket}")
    private String bucket;

    @Value("${naver.api.client-id}")
    private String clientId;

    @Value("${naver.api.client-secret}")
    private String clientSecret;

    @Transactional
    public ArticleViewDto saveArticleView(UUID articleId, UUID userId) {
        NewsArticle article = newsArticleRepository.findByIdAndDeletedFalse(articleId)
            .orElseThrow(() ->{
                ErrorDetail detail = new ErrorDetail("UUID", "articleId", articleId.toString());
                return new CustomException(ErrorCode.RESOURCE_NOT_FOUND, detail, ExceptionType.NEWSARTICLE);
            });

        // 유저 조회
        User user = userRepository.findById(userId)
            .orElseThrow(() -> {
                ErrorDetail detail = new ErrorDetail("UUID", "userId", userId.toString());
                return new CustomException(ErrorCode.RESOURCE_NOT_FOUND, detail, ExceptionType.USER);
            });

        // 중복 조회 방지
        Optional<ArticleView> optional = articleViewRepository.findByArticleAndUser(article, user);
        if (optional.isPresent()) {
            return ArticleViewMapper.toDto(optional.get());
        }

        // 조회 기록 저장 및 viewCount 증가
        ArticleView view = articleViewRepository.save(new ArticleView(article, user));
        article.increaseViewCount();

        // Mongo 활동 내역 동기화
        ArticleViewDto viewDto = ArticleViewMapper.toDto(view);
        activityDocumentUpdater.addRecentArticleView(user.getId(), viewDto);

        return ArticleViewMapper.toDto(view);
    }

    @Transactional(readOnly = true)
    public CursorPageResponseArticleDto searchArticles(
        String keyword,
        UUID interestId,
        List<String> sourceIn,
        LocalDateTime publishDateFrom,
        LocalDateTime publishDateTo,
        String orderBy,
        String direction,
        String cursor,
        String after,
        Integer limit,
        UUID requestUserId) {

        // Repository에 로직 위임
        List<NewsArticle> result = newsArticleRepository.searchArticles(
            keyword, interestId, sourceIn,
            publishDateFrom, publishDateTo,
            orderBy, direction, after, limit
        );

        // 페이지 조정
        boolean hasNext = result.size() > limit;
        List<NewsArticle> pageContent = hasNext ? result.subList(0, limit) : result;

        List<ArticleDto> articles = pageContent.stream()
            .map(NewsArticleMapper::toArticleDto)
            .toList();

        String nextCursor = hasNext ? encodeCursor(articles.get(articles.size() - 1)) : null;
        LocalDateTime nextAfter = hasNext ? articles.get(articles.size() - 1).publishDate() : null;

        return new CursorPageResponseArticleDto(articles, nextCursor, nextAfter, limit, result.size(), hasNext);
    }

    @Transactional(readOnly = true)
    public List<String> getSources() {
        List<String> sources = newsArticleRepository.findDistinctSources();
        return sources;
    }

    @Transactional
    public void deleteLogically(UUID articleId) {
        NewsArticle article = newsArticleRepository.findByIdAndDeletedFalse(articleId)
            .orElseThrow(() ->{
                ErrorDetail detail = new ErrorDetail("UUID", "articleId", articleId.toString());
                return new CustomException(ErrorCode.RESOURCE_NOT_FOUND, detail, ExceptionType.NEWSARTICLE);
            });

        article.markAsDeleted(); // 엔티티에서 delete를 true로 변경
    }

    @Transactional
    public void deletePhysically(UUID articleId) {
        NewsArticle article = newsArticleRepository.findById(articleId)
            .orElseThrow(() -> {
                ErrorDetail detail = new ErrorDetail("UUID", "articleId", articleId.toString());
                return new CustomException(ErrorCode.RESOURCE_NOT_FOUND, detail, ExceptionType.NEWSARTICLE);
            });

        articleViewRepository.deleteByArticle(article);
        newsArticleRepository.delete(article);
    }

    // 기사가 키워드를 포함하고 있는지 확인
    @Transactional(readOnly = true)
    public boolean containsKeyword(String title, String desc) {
        List<Interest> interests = interestRepository.findAllWithKeywords();

        List<String> keywords = interests.stream()
            .flatMap(interest -> interest.getKeywords().stream())
            .distinct()
            .collect(Collectors.toList());
        return keywords.stream().anyMatch(k -> title.contains(k) || desc.contains(k));
    }

    // 뉴스 기사 저장(중복 방지)
    @Transactional
    public void saveIfNotExists(NewsArticleRequestDto dto) {
        boolean exists = newsArticleRepository.existsByOriginalLink(dto.originalLink());
        if (!exists) {
            List<Interest> interests = interestRepository.findAllById(dto.interestIds());
            NewsArticle article = NewsArticle.builder()
                .title(dto.title())
                .originalLink(dto.originalLink())
                .summary(dto.summary())
                .date(dto.date())
                .source(dto.source())
                .interests(interests)
                .viewCount(0)
                .deleted(false)
                .build();

            newsArticleRepository.save(article);
        }
    }

    // 마지막 요소의 커서 인코딩
    public String encodeCursor(ArticleDto lastArticle) {
        // publishDate 기준
        return Base64.getEncoder().encodeToString(
            lastArticle.publishDate().toString().getBytes(StandardCharsets.UTF_8)
        );
    }


    // 네이버 api에서 뉴스 기사 받아오기
    @Transactional(readOnly = true)
    public List<NewsArticleRequestDto> collectFromNaver() {
        List<Interest> interests = interestRepository.findAllWithKeywords();
        Map<String, NaverNewsItem> itemMap = new HashMap<>();
        Map<String, Set<UUID>> articleInterestMap = new HashMap<>();

        for (Interest interest : interests) {
            for (String keyword : interest.getKeywords()) {
                try {
                    String url = "https://openapi.naver.com/v1/search/news.json?query=" + keyword
                        + "&display=100&sort=date"; // 네이버 뉴스 API 기본 응답은 10개, 최대 100개

                    HttpHeaders headers = new HttpHeaders();
                    headers.set("X-Naver-Client-Id", clientId);  // 필드 주입 or @Value 필요
                    headers.set("X-Naver-Client-Secret", clientSecret);

                    HttpEntity<Void> request = new HttpEntity<>(headers);
                    ResponseEntity<NaverResponseDto> response = new RestTemplate().exchange(
                        url, HttpMethod.GET, request, NaverResponseDto.class);

                    List<NaverNewsItem> items = response.getBody().getItems();

                    for (NaverNewsItem item : items) {
                        String link = item.getLink();

                        itemMap.putIfAbsent(link, item);
                        articleInterestMap
                            .computeIfAbsent(link, k -> new HashSet<>())
                            .add(interest.getId());
                    }
                } catch (Exception e) {
                    log.error("[Naver API뉴스 수집 실패] keyword={}, message={}", keyword, e.getMessage(), e);
                }
            }
        }

        // 결과 리스트 구성
        List<NewsArticleRequestDto> result = new ArrayList<>();
        for (Map.Entry<String, Set<UUID>> entry : articleInterestMap.entrySet()) {
            String link = entry.getKey();
            Set<UUID> interestIds = entry.getValue();

            NaverNewsItem item = itemMap.get(link);
            if (item == null) continue;

            result.add(item.toDto(new ArrayList<>(interestIds)));
        }

        return result;
    }

    // 여러 RSS에서 뉴스 기사 받아오기
    @Transactional(readOnly = true)
    public List<NewsArticleRequestDto> collectFromRss() {
        Map<Interest, List<String>> interestKeywordMap = interestService.getInterestKeywordMap();

        Map<String, String> sources = Map.of(
            "https://www.hankyung.com/feed/all-news", "한국경제",
            "https://www.chosun.com/arc/outboundfeeds/rss/?outputType=xml", "조선일보",
            "http://www.yonhapnewstv.co.kr/category/news/headline/feed/", "연합뉴스TV"
        );

        List<NewsArticleRequestDto> result = new ArrayList<>();

        for (Map.Entry<String, String> entry : sources.entrySet()) {
            String rssUrl = entry.getKey();
            String source = entry.getValue();

            try {
                Document doc = Jsoup.connect(rssUrl).get();
                Elements items = doc.select("item");

                for (Element item : items) {
                    String title = item.selectFirst("title").text();
                    String link = item.selectFirst("link").text();
                    String pubDate = item.selectFirst("pubDate").text();
                    LocalDateTime date = rssUtils.parseRfc822(pubDate);

                    // 요약 추출
                    String summary = rssUtils.extractSummary(item, title);

                    // 키워드 매칭된 관심사 추출
                    List<UUID> matchedInterestIds = interestKeywordMap.entrySet().stream()
                        .filter(e -> e.getValue().stream().anyMatch(k -> title.contains(k) || summary.contains(k)))
                        .map(e -> e.getKey().getId())
                        .distinct()
                        .toList();

                    if (!matchedInterestIds.isEmpty()) {
                        result.add(new NewsArticleRequestDto(
                            title,
                            link,
                            summary,
                            date,
                            source,
                            matchedInterestIds
                        ));
                    }
                }

            } catch (IOException e) {
                log.error("[RSS 뉴스 수집 실패] rssUrl={}, message={}", rssUrl, e.getMessage(), e);
            }
        }

        return result;
    }

}
