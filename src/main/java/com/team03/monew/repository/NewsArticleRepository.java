package com.team03.monew.repository;

import com.team03.monew.entity.NewsArticle;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface NewsArticleRepository extends JpaRepository<NewsArticle, Long> {
    // 삭제되지 않은 기사만 찾기. 논리 삭제 처리된 데이터 제외하고 조회
    Optional<NewsArticle> findByIdAndDeletedFalse(Long id);

}