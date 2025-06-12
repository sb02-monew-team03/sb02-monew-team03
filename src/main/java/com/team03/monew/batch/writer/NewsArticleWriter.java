package com.team03.monew.batch.writer;

import com.team03.monew.dto.newsArticle.request.NewsArticleRequestDto;
import com.team03.monew.service.news.NewsArticleService;
import lombok.RequiredArgsConstructor;
import org.springframework.batch.item.Chunk;
import org.springframework.batch.item.ItemWriter;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class NewsArticleWriter implements ItemWriter<NewsArticleRequestDto> {

    private final NewsArticleService newsArticleService;

    @Override
    public void write(Chunk<? extends NewsArticleRequestDto> items) throws Exception {
        for (NewsArticleRequestDto dto : items) {
            newsArticleService.saveIfNotExists(dto);
        }
    }
}