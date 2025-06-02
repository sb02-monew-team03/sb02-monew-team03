package com.team03.monew.storage;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.team03.monew.dto.newsArticle.response.NewsBackupDto;
import java.io.IOException;
import java.io.InputStream;
import java.time.LocalDate;
import java.util.List;
import org.springframework.stereotype.Component;

// aws 설정이 진행되지 않아 우선 로컬로 구현
@Component
//@Profile("local")
public class LocalBackupStorage implements BackupStorage {

    @Override
    public List<NewsBackupDto> loadBackup(LocalDate date) {
        String filename = "mock-backup/news-" + date + ".json";

        try (InputStream is = getClass().getClassLoader().getResourceAsStream(filename)) {
            if (is == null) {
                throw new RuntimeException("백업 파일이 classpath에서 발견되지 않음: " + filename);
            }

            ObjectMapper mapper = new ObjectMapper();
            mapper.registerModule(new JavaTimeModule()); // LocalDateTime 대응
            mapper.disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES);

            return mapper.readValue(is, new TypeReference<>() {});
        } catch (IOException e) {
            throw new RuntimeException("백업 파일 로딩 실패: " + filename, e);
        }
    }
}
