package com.team03.monew.batch.processor;

import com.team03.monew.dto.newsArticle.request.NewsArticleRequestDto;
import com.team03.monew.entity.Interest;
import com.team03.monew.service.InterestService;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;
import lombok.RequiredArgsConstructor;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class NewsArticleProcessor implements
    ItemProcessor<NewsArticleRequestDto, NewsArticleRequestDto> {

    private final InterestService interestService;

    @Override
    public NewsArticleRequestDto process(NewsArticleRequestDto dto) {
        // summary가 비어있거나 깨졌으면 title로 대체
        String summary = cleanOrFallbackSummary(dto.summary(), dto.title());

        // 키워드 강조 적용
        Map<Interest, List<String>> interestKeywordMap = interestService.getInterestKeywordMap();
        List<String> matchedKeywords = interestKeywordMap.entrySet().stream()
            .filter(e -> dto.interestIds().contains(e.getKey().getId()))
            .flatMap(e -> e.getValue().stream())
            .distinct()
            .toList();

        String highlightedTitle = highlightKeywords(dto.title(), matchedKeywords);
        String highlightedSummary = highlightKeywords(summary, matchedKeywords);

        // 새로운 DTO로 반환 (record는 불변이므로 새로 생성 필요)
        return new NewsArticleRequestDto(
            highlightedTitle,
            dto.originalLink(),
            highlightedSummary,
            dto.date(),
            dto.source(),
            dto.interestIds()
        );
    }

    private String cleanOrFallbackSummary(String summary, String fallback) {
        if (summary == null || summary.isBlank() || summary.length() < 10 || containsBrokenChars(summary)) {
            return fallback;
        }
        return summary;
    }

    private boolean containsBrokenChars(String text) {
        return text.contains("�"); // 깨진 문자 대표 패턴
    }

    private String highlightKeywords(String text, List<String> keywords) {
        String result = text;
        for (String keyword : keywords) {
            result = result.replaceAll("(?i)(" + Pattern.quote(keyword) + ")", "<b>$1</b>");
        }
        return result;
    }
}

