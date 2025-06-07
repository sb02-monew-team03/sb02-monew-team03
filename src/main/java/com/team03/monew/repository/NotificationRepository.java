package com.team03.monew.repository;

import com.team03.monew.entity.Notification;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface NotificationRepository extends JpaRepository<Notification, UUID> {

  // 1. cursor + after 둘 다 있음
  @Query("""
      SELECT n FROM Notification n
      WHERE n.user.id = :userId AND n.id < :cursor AND n.createdAt < :after
      ORDER BY n.createdAt DESC
  """)
  List<Notification> findByUserIdAndCursorAndAfter(
          @Param("userId") UUID userId,
          @Param("cursor") UUID cursor,
          @Param("after") LocalDateTime after,
          Pageable pageable
  );

  // 2. cursor만 있음
  @Query("""
      SELECT n FROM Notification n
      WHERE n.user.id = :userId AND n.id < :cursor
      ORDER BY n.createdAt DESC
  """)
  List<Notification> findByUserIdAndCursor(
          @Param("userId") UUID userId,
          @Param("cursor") UUID cursor,
          Pageable pageable
  );

  // 3. after만 있음
  @Query("""
      SELECT n FROM Notification n
      WHERE n.user.id = :userId AND n.createdAt < :after
      ORDER BY n.createdAt DESC
  """)
  List<Notification> findByUserIdAndAfter(
          @Param("userId") UUID userId,
          @Param("after") LocalDateTime after,
          Pageable pageable
  );

  // 4. 아무 필터도 없음
  @Query("""
      SELECT n FROM Notification n
      WHERE n.user.id = :userId
      ORDER BY n.createdAt DESC
  """)
  List<Notification> findByUserId(
          @Param("userId") UUID userId,
          Pageable pageable
  );

  Optional<Notification> findByIdAndUserId(UUID id, UUID userId);

  @Modifying
  @Query("UPDATE Notification n SET n.checked = true, n.updatedAt = CURRENT_TIMESTAMP WHERE n.user.id = :userId")
  void updateAllCheckedByUserId(@Param("userId") UUID userId);

  void deleteByCheckedIsTrueAndUpdatedAtBefore(LocalDateTime dateTime);

  long countByUserId(UUID userId);
}
