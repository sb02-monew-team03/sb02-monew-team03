package com.team03.monew.controller;

import com.team03.monew.dto.newsArticle.ArticleViewDto;
import com.team03.monew.service.NewsArticleService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/articles")
@RequiredArgsConstructor
public class NewsArticleController {

    private final NewsArticleService newsArticleService;

    @PostMapping("/{articleId}/article-views")
    public ResponseEntity<ArticleViewDto> saveArticleView(
        @PathVariable Long articleId,
        @RequestHeader("MoNew-Request-User-ID") Long userId
    ) {
        ArticleViewDto dto = newsArticleService.saveArticleView(articleId, userId);
        return ResponseEntity.ok(dto);
    }
}
