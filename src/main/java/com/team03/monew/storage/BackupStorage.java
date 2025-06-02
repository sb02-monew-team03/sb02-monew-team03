package com.team03.monew.storage;

import com.team03.monew.dto.newsArticle.response.NewsBackupDto;
import java.time.LocalDate;
import java.util.List;

public interface BackupStorage {
    List<NewsBackupDto> loadBackup(LocalDate date);
}