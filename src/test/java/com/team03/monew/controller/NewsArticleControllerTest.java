package com.team03.monew.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.willDoNothing;
import static org.mockito.BDDMockito.willThrow;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.team03.monew.dto.newsArticle.response.ArticleDto;
import com.team03.monew.dto.newsArticle.response.ArticleViewDto;
import com.team03.monew.dto.newsArticle.response.CursorPageResponseArticleDto;
import com.team03.monew.dto.newsArticle.response.SourcesResponseDto;
import com.team03.monew.exception.CustomException;
import com.team03.monew.exception.ErrorCode;
import com.team03.monew.exception.ErrorDetail;
import com.team03.monew.exception.ExceptionType;
import com.team03.monew.service.news.NewsArticleService;
import java.time.LocalDateTime;
import java.util.List;
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

    @Test
    @DisplayName("뉴스 기사 목록 조회 성공")
    void getArticles_Success() throws Exception {
        CursorPageResponseArticleDto response = createMockResponse();
        given(newsArticleService.searchArticles(any(), any(), any(), any(), any(), any(), any(), any(), any(), any(), any()))
            .willReturn(response);

        mockMvc.perform(get("/api/articles")
                .param("orderBy", "publishDate")
                .param("direction", "DESC")
                .header("Monew-Request-User-ID", UUID.randomUUID().toString()))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.content[0].title").value("AI 혁명: OpenAI의 미래"));
    }

    @Test
    @DisplayName("정렬 기준이 잘못 되었을 경우")
    void getArticles_InvalidOrderBy_BadRequest() throws Exception {
        mockMvc.perform(get("/api/articles")
                .param("orderBy", "wrong")
                .param("direction", "DESC")
                .header("Monew-Request-User-ID", UUID.randomUUID().toString()))
            .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("출처 목록 조회 성공")
    void getSources_Success() throws Exception {
        List<String> mockSources = List.of("NAVER", "연합뉴스");
        given(newsArticleService.getSources())
            .willReturn(new SourcesResponseDto(mockSources));

        mockMvc.perform(get("/api/articles/sources"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.sources[0]").value("NAVER"))
            .andExpect(jsonPath("$.sources[1]").value("연합뉴스"));
    }

    @Test
    @DisplayName("출처 목록 조회 실패 - 서버 에러 발생 시 500 반환")
    void getSources_Fail() throws Exception {
        given(newsArticleService.getSources())
            .willThrow(new IllegalStateException("DB 연결 오류"));

        mockMvc.perform(get("/api/articles/sources"))
            .andExpect(status().isInternalServerError())
            .andExpect(jsonPath("$.code").value("INTERNAL_SERVER_ERROR"))
            .andExpect(jsonPath("$.exceptionType").value("IllegalStateException"));
    }

    // 논리 삭제
    @Test
    @DisplayName("뉴스 기사 논리 삭제 성공")
    void deleteArticle_Success() throws Exception {
        UUID articleId = UUID.randomUUID();

        // 서비스 계층이 void니까 별도 stubbing 없이 진행
        willDoNothing().given(newsArticleService).deleteLogically(articleId);

        mockMvc.perform(delete("/api/articles/{id}", articleId))
            .andExpect(status().isNoContent()); // 204
    }

    @DisplayName("뉴스 기사 논리 삭제 실패 - 존재하지 않는 기사")
    @Test
    void deleteArticle_Fail_NotFound() throws Exception {
        UUID articleId = UUID.randomUUID();

        willThrow(new CustomException(
            ErrorCode.RESOURCE_NOT_FOUND,
            new ErrorDetail("UUID", "articleId", articleId.toString()),
            ExceptionType.NEWSARTICLE
        )).given(newsArticleService).deleteLogically(articleId);

        mockMvc.perform(delete("/api/articles/{id}", articleId))
            .andExpect(status().isNotFound())
            .andExpect(jsonPath("$.code").value("RESOURCE_NOT_FOUND"))
            .andExpect(jsonPath("$.exceptionType").value("NEWSARTICLE"))
            .andExpect(jsonPath("$.details.parameter").value("articleId"))
            .andExpect(jsonPath("$.details.value").value(articleId.toString()));
    }

    @DisplayName("뉴스 기사 물리 삭제 성공")
    @Test
    void deleteArticlePhysically_Success() throws Exception {
        UUID articleId = UUID.randomUUID();

        willDoNothing().given(newsArticleService).deletePhysically(articleId);

        mockMvc.perform(delete("/api/articles/{id}/hard", articleId))
            .andExpect(status().isNoContent());
    }

    @DisplayName("뉴스 기사 물리 삭제 실패 - 존재하지 않는 기사")
    @Test
    void deleteArticlePhysically_Fail_NotFound() throws Exception {
        UUID articleId = UUID.randomUUID();

        willThrow(new CustomException(
            ErrorCode.RESOURCE_NOT_FOUND,
            new ErrorDetail("UUID", "articleId", articleId.toString()),
            ExceptionType.NEWSARTICLE
        )).given(newsArticleService).deletePhysically(articleId);

        mockMvc.perform(delete("/api/articles/{id}/hard", articleId))
            .andExpect(status().isNotFound())
            .andExpect(jsonPath("$.code").value("RESOURCE_NOT_FOUND"))
            .andExpect(jsonPath("$.exceptionType").value("NEWSARTICLE"))
            .andExpect(jsonPath("$.details.parameter").value("articleId"))
            .andExpect(jsonPath("$.details.value").value(articleId.toString()));
    }

    public static ArticleDto createMockArticle() {
        return new ArticleDto(
            UUID.randomUUID(),
            "naver",
            "https://example.com/thumbnail.jpg",
            "AI 혁명: OpenAI의 미래",
            LocalDateTime.of(2025, 5, 30, 10, 0),
            "OpenAI는 GPT-5를 공개하며...",
            15,
            120,
            true
        );
    }

    public static CursorPageResponseArticleDto createMockResponse() {
        return new CursorPageResponseArticleDto(
            List.of(createMockArticle()),
            "nextCursor123",
            LocalDateTime.of(2025, 5, 30, 9, 0),
            50,
            100,
            true
        );
    }

}
