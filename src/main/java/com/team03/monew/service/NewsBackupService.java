package com.team03.monew.service;

import com.team03.monew.entity.NewsArticle;
import com.team03.monew.repository.NewsArticleRepository;
import com.team03.monew.storage.BackupStorage;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class NewsBackupService {

    private final NewsArticleRepository newsArticleRepository;
    private final BackupStorage backupStorage;

    public void backupArticlesFor(LocalDate date) {
        LocalDateTime start = date.atStartOfDay();                      // 00:00:00
        LocalDateTime end = date.plusDays(1).atStartOfDay();           // 다음날 00:00:00

        List<NewsArticle> articles = newsArticleRepository.findByDateBetween(start, end);
        backupStorage.backupArticles(articles, date);
    }
}
