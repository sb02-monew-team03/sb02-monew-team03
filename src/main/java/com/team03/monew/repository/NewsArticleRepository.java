package com.team03.monew.repository;

import com.team03.monew.entity.NewsArticle;
import com.team03.monew.repository.custom.NewsArticleRepositoryCustom;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface NewsArticleRepository extends JpaRepository<NewsArticle, UUID>,
    NewsArticleRepositoryCustom {
    // 삭제되지 않은 기사만 찾기. 논리 삭제 처리된 데이터 제외하고 조회
    Optional<NewsArticle> findByIdAndDeletedFalse(UUID id);

    // 출처 목록 조회용
    @Query("SELECT DISTINCT n.source FROM NewsArticle n WHERE n.deleted = false")
    List<String> findDistinctSources();

    // 복구용. 이미 데이터베이스에 존재하는지 확인
    boolean existsByOriginalLink(String originalLink);
}