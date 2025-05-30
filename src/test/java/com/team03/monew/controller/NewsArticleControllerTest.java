package com.team03.monew.controller;

import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.team03.monew.dto.newsArticle.ArticleViewDto;
import com.team03.monew.exception.CustomException;
import com.team03.monew.exception.ErrorCode;
import com.team03.monew.exception.ErrorDetail;
import com.team03.monew.exception.ExceptionType;
import com.team03.monew.service.NewsArticleService;
import java.time.LocalDateTime;
import java.util.UUID;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

@WebMvcTest(controllers = NewsArticleController.class)
class NewsArticleControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private NewsArticleService newsArticleService;

    @Test
    @DisplayName("기사 뷰 등록 성공")
    void saveArticleView_Success() throws Exception {
        // Given
        UUID articleId = UUID.randomUUID();
        UUID userId = UUID.randomUUID();

        ArticleViewDto dto = new ArticleViewDto(
            UUID.randomUUID(),         // viewId
            userId,                    // viewedBy
            LocalDateTime.now(),       // createdAt
            articleId,                 // articleId
            "NAVER",                   // source
            "https://naver.com/article", // sourceUrl
            "기사 제목입니다",            // articleTitle
            LocalDateTime.now(),       // articlePublishedDate
            "기사 요약입니다",           // articleSummary
            5,                         // commentCount
            100                        // viewCount
        );

        given(newsArticleService.saveArticleView(eq(articleId), eq(userId)))
            .willReturn(dto);

        // When & Then
        mockMvc.perform(post("/api/articles/{articleId}/article-views", articleId)
                .header("MoNew-Request-User-ID", userId))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.articleId").value(articleId.toString()))
            .andExpect(jsonPath("$.viewedBy").value(userId.toString()))
            .andExpect(jsonPath("$.articleTitle").value("기사 제목입니다"));
    }

    @Test
    @DisplayName("존재하지 않는 뉴스 기사 ID로 조회 시 404 응답")
    void saveArticleView_NotFound() throws Exception {
        // Given
        UUID articleId = UUID.randomUUID();
        UUID userId = UUID.randomUUID();
        ErrorDetail detail = new ErrorDetail("UUID", "articleId", articleId.toString());

        given(newsArticleService.saveArticleView(eq(articleId), eq(userId)))
            .willThrow(new CustomException(ErrorCode.RESOURCE_NOT_FOUND, detail, ExceptionType.NEWSARTICLE));

        // When & Then
        mockMvc.perform(post("/api/articles/{articleId}/article-views", articleId)
                .header("MoNew-Request-User-ID", userId))
            .andExpect(status().isNotFound());
    }

}
