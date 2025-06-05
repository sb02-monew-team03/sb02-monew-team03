package com.team03.monew.storage;

import com.team03.monew.entity.NewsArticle;
import java.time.LocalDate;
import java.util.List;

public interface BackupStorage {
    void backupArticles(List<NewsArticle> articles, LocalDate date);
}