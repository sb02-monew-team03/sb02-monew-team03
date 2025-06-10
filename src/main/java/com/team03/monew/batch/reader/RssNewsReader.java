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
public class RssNewsReader implements ItemReader<NewsArticleRequestDto> {
    // RSS Reader
    private final NewsArticleService newsArticleService;

    private List<NewsArticleRequestDto> articles;
    private int currentIndex = 0;

    @PostConstruct
    public void init() {
        this.articles = newsArticleService.collectFromRss(); // RSS 로직 분리
    }

    @Override
    public NewsArticleRequestDto read() {
        return (currentIndex < articles.size()) ? articles.get(currentIndex++) : null;
    }

}
