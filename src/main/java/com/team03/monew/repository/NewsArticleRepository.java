package com.team03.monew.repository;

import com.team03.monew.entity.NewsArticle;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface NewsArticleRepository extends JpaRepository<NewsArticle, UUID> {
  // 제목 또는 요약에서 키워드 검색 (부분 일치)
//  @Query("SELECT n FROM NewsArticle n WHERE n.deleted = false AND (n.title LIKE %:keyword% OR n.summary LIKE %:keyword%)")
//  List<NewsArticle> searchByTitleOrSummary(@Param("keyword") String keyword);
//
//  // 출처, 관심사, 날짜 모두 만족하는 경우 검색
//  List<NewsArticle> findAllByDeletedFalseAndSourceAndInterestIdAndDate(
//      String source, UUID interestId, LocalDateTime date
//  );
//
//  // 내림차순 정렬 및 페이지네이션, 날짜, 조회 수, 댓글 수
//  Slice<NewsArticle> findByDeletedFalseAndDateBeforeOrderByDateDesc(LocalDateTime cursor, Pageable pageable);
//  Slice<NewsArticle> findByDeletedFalseAndViewCountLessThanOrderByViewCountDesc(int cursor, Pageable pageable);
//  Slice<NewsArticle> findByDeletedFalseAndCommentsSizeLessThanOrderByCommentsSizeDesc(int cursor, Pageable pageable);
//
//  // 날짜 단위로 데이터 백업
//  List<NewsArticle> findAllByDateBetweenAndDeletedFalse(LocalDateTime start, LocalDateTime end);

}
