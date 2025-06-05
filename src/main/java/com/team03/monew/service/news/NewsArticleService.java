package com.team03.monew.service.news;

import com.querydsl.jpa.impl.JPAQueryFactory;
import com.team03.monew.dto.newsArticle.mapper.ArticleViewMapper;
import com.team03.monew.dto.newsArticle.mapper.NewsArticleMapper;
import com.team03.monew.dto.newsArticle.request.NewsArticleRequestDto;
import com.team03.monew.dto.newsArticle.response.ArticleDto;
import com.team03.monew.dto.newsArticle.response.ArticleViewDto;
import com.team03.monew.dto.newsArticle.response.CursorPageResponseArticleDto;
import com.team03.monew.dto.newsArticle.response.SourcesResponseDto;
import com.team03.monew.entity.ArticleView;
import com.team03.monew.entity.Interest;
import com.team03.monew.entity.NewsArticle;
import com.team03.monew.entity.User;
import com.team03.monew.exception.CustomException;
import com.team03.monew.exception.ErrorCode;
import com.team03.monew.exception.ErrorDetail;
import com.team03.monew.exception.ExceptionType;
import com.team03.monew.repository.ArticleViewRepository;
import com.team03.monew.repository.InterestRepository;
import com.team03.monew.repository.NewsArticleRepository;
import com.team03.monew.repository.UserRepository;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.Base64;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class NewsArticleService {

    private final NewsArticleRepository newsArticleRepository;
    private final ArticleViewRepository articleViewRepository;
    private final InterestRepository interestRepository;
    private final UserRepository userRepository;
    private final JPAQueryFactory queryFactory;

    @Value("${aws.s3.bucket}")
    private String bucket;

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
    public SourcesResponseDto getSources() {
        List<String> sources = newsArticleRepository.findDistinctSources();
        return new SourcesResponseDto(sources);
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
        List<String> keywords = interestRepository.findAllKeywords(); // "AI", "정치", "게임" 등
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

}
