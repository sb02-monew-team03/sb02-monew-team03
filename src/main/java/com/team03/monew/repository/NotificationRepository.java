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

  @Query("""
        SELECT n FROM Notification n
        WHERE n.user.id = :userId
        AND (:cursor IS NULL OR n.id < :cursor)
        AND (:after IS NULL OR n.createdAt < :after)
        ORDER BY n.createdAt DESC
    """)
  List<Notification> findByCursorAndAfter(
      @Param("userId") UUID userId,
      @Param("cursor") UUID cursor,
      @Param("after") LocalDateTime after,
      Pageable pageable
  );

  @Query("SELECT n FROM Notification n WHERE n.user.id = :userId AND (:cursor IS NULL OR n.id < :cursor) AND (:after IS NULL OR n.createdAt < :after) ORDER BY n.createdAt DESC")
  List<Notification> findByUserWithCursor(@Param("userId") UUID userId,
      @Param("cursor") String cursor,
      @Param("after") LocalDateTime after,
      @Param("limit") int limit);

  Optional<Notification> findByIdAndUserId(UUID id, UUID userId);

  @Modifying
  @Query("UPDATE Notification n SET n.checked = true, n.updatedAt = CURRENT_TIMESTAMP WHERE n.user.id = :userId")
  void updateAllCheckedByUserId(@Param("userId") UUID userId);

  long countByUserId(UUID userId);
}