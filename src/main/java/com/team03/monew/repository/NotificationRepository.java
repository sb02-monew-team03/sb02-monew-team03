package com.team03.monew.repository;

import com.team03.monew.entity.Notification;
import java.time.LocalDateTime;
import java.util.List;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface NotificationRepository extends JpaRepository<Notification, Long> {

  @Query("""
        SELECT n FROM Notification n
        WHERE n.user.id = :userId
        AND (:cursor IS NULL OR n.id < :cursor)
        AND (:after IS NULL OR n.createdAt < :after)
        ORDER BY n.createdAt DESC
    """)
  List<Notification> findByCursorAndAfter(
      @Param("userId") Long userId,
      @Param("cursor") Long cursor,
      @Param("after") LocalDateTime after,
      Pageable pageable
  );

  long countByUserId(Long userId);
}