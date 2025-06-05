package com.team03.monew.controller;

import com.team03.monew.dto.newsArticle.response.ArticleRestoreResultDto;
import com.team03.monew.dto.newsArticle.response.ArticleViewDto;
import com.team03.monew.dto.newsArticle.response.CursorPageResponseArticleDto;
import com.team03.monew.dto.newsArticle.response.SourcesResponseDto;
import com.team03.monew.exception.CustomException;
import com.team03.monew.exception.ErrorCode;
import com.team03.monew.exception.ErrorDetail;
import com.team03.monew.exception.ExceptionType;
import com.team03.monew.service.NewsArticleService;
import com.team03.monew.service.NewsRestoreService;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/articles")
@RequiredArgsConstructor
public class NewsArticleController {

    private final NewsArticleService newsArticleService;
    private final NewsRestoreService newsRestoreService;

    @PostMapping("/{articleId}/article-views")
    public ResponseEntity<ArticleViewDto> saveArticleView(
        @PathVariable UUID articleId,
        @RequestHeader("MoNew-Request-User-ID") UUID userId
    ) {
        ArticleViewDto dto = newsArticleService.saveArticleView(articleId, userId);
        return ResponseEntity.ok(dto);
    }

    @GetMapping
    public ResponseEntity<CursorPageResponseArticleDto> getArticles(
        @RequestParam(required = false) String keyword,
        @RequestParam(required = false) UUID interestId,
        @RequestParam(required = false) List<String> sourceIn,
        @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime publishDateFrom,
        @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime publishDateTo,
        @RequestParam String orderBy,
        @RequestParam String direction,
        @RequestParam(required = false) String cursor,
        @RequestParam(required = false) String after,
        @RequestParam(defaultValue = "50") Integer limit,
        @RequestHeader("Monew-Request-User-ID") UUID requestUserId
    ){
        validateOrderBy(orderBy);
        validateDirection(direction);

        CursorPageResponseArticleDto result = newsArticleService.searchArticles(
            keyword, interestId, sourceIn, publishDateFrom, publishDateTo,
            orderBy, direction, cursor, after, limit, requestUserId
        );
        return ResponseEntity.ok(result);
    }

    @GetMapping("/sources")
    public ResponseEntity<SourcesResponseDto> getSources() {
        return ResponseEntity.ok(newsArticleService.getSources());
    }

    @DeleteMapping("/{articleId}")
    public ResponseEntity<Void> deleteArticleLogical(@PathVariable UUID articleId) {
        newsArticleService.deleteLogically(articleId);
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/{articleId}/hard")
    public ResponseEntity<Void> deleteArticleHard(@PathVariable UUID articleId) {
        newsArticleService.deletePhysically(articleId);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/restore")
    public ResponseEntity<List<ArticleRestoreResultDto>> restoreArticles(
        @RequestParam("from") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate from,
        @RequestParam("to") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate to) {

        List<ArticleRestoreResultDto> results = newsRestoreService.restore(from, to);
        return ResponseEntity.ok(results);
    }

    private static final Set<String> VALID_ORDER_BY = Set.of("publishDate", "commentCount", "viewCount");
    private static final Set<String> VALID_DIRECTION = Set.of("ASC", "DESC");

    private void validateOrderBy(String orderBy) {
        if (!VALID_ORDER_BY.contains(orderBy)) {
            ErrorDetail detail = new ErrorDetail("String", "orderBy", orderBy);
            throw new CustomException(ErrorCode.INVALID_INPUT_VALUE, detail, ExceptionType.NEWSARTICLE);
        }
    }

    private void validateDirection(String direction) {
        if (!VALID_DIRECTION.contains(direction.toUpperCase())) {
            ErrorDetail detail = new ErrorDetail("String", "direction", direction);
            throw new CustomException(ErrorCode.INVALID_INPUT_VALUE, detail, ExceptionType.NEWSARTICLE);
        }
    }
}
