package com.team03.monew.batch.reader;

import com.team03.monew.dto.newsArticle.request.NewsArticleRequestDto;
import com.team03.monew.service.news.NewsArticleService;
import jakarta.annotation.PostConstruct;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.item.ItemReader;
import org.springframework.stereotype.Component;

@Component
@StepScope
@RequiredArgsConstructor
public class NaverNewsReader implements ItemReader<NewsArticleRequestDto> {
    // Naver API Reader
    private final NewsArticleService newsArticleService;

    private List<NewsArticleRequestDto> articles;
    private int currentIndex = 0;

    @PostConstruct
    public void init() {
        // collect() 내부 로직을 리팩토링해서 아래 메서드로 추출해야 함
        this.articles = newsArticleService.collectFromNaver();
    }

    @Override
    public NewsArticleRequestDto read() {
        return (currentIndex < articles.size()) ? articles.get(currentIndex++) : null;
    }
}
