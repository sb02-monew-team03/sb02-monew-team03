package com.team03.monew.external.naver;

import com.team03.monew.dto.newsArticle.request.NewsArticleRequestDto;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.UUID;
import lombok.Data;
import org.jsoup.Jsoup;

@Data
public class NaverNewsItem {
    private String title;
    private String originallink;
    private String link;
    private String description;
    private String pubDate;

    public NewsArticleRequestDto toDto(List<UUID> interestIds) {
        return new NewsArticleRequestDto(
            this.title,
            this.link,
            cleanSummary(this.description),
            parse(this.pubDate),
            "NAVER",
            interestIds
        );
    }

    private String removeTags(String input) {
        if (input == null) return "";
        return input.replaceAll("<[^>]*>", "").trim();
    }

    private LocalDateTime parse(String dateStr) {
        DateTimeFormatter formatter = DateTimeFormatter.RFC_1123_DATE_TIME;
        return ZonedDateTime.parse(dateStr, formatter).withZoneSameInstant(ZoneId.of("Asia/Seoul")).toLocalDateTime();
    }

    private String cleanSummary(String input) {
        if (input == null || input.isBlank()) return "";

        // HTML 태그 제거
        String cleaned = Jsoup.parse(input).text().trim();

        // HTML 엔티티 디코딩 (&quot;, &amp; 등)
        cleaned = org.apache.commons.text.StringEscapeUtils.unescapeHtml4(cleaned);

        // URL 디코딩 (%EC%8A%A4 → 유니코드 문자)
        try {
            cleaned = java.net.URLDecoder.decode(cleaned, StandardCharsets.UTF_8);
        } catch (IllegalArgumentException e) {
            // 디코딩 실패한 경우 원본 사용
        }

        // 불필요한 링크 제거
        cleaned = cleaned.replaceAll("https?://\\S+", "");

        // 광고 문구 패턴 제거
        String[] banPhrases = {"더보기", "자세히 보기", "관련 기사", "관련 뉴스", "사진=|영상=", "출처="};
        for (String phrase : banPhrases) {
            if (cleaned.contains(phrase)) {
                cleaned = cleaned.replaceAll(".*" + phrase + ".*", "");
            }
        }
        // PDF/문서 링크 제거
        if (cleaned.matches(".*\\.(pdf|xls|doc)[^\\s]*.*")) return "";

        // 요약에 언론사 이름만 반복 → 의미 없음
        if (cleaned.matches(".*(SBS|연합뉴스|조선일보|채널A|MBC|KBS).*보기.*")) return "";

        // 특수문자, 공백만 있는 경우 제거
        if (cleaned.replaceAll("[^a-zA-Z가-힣0-9]", "").isBlank() || cleaned.length() < 10) {
            return "";
        }

        return cleaned;
    }
}
