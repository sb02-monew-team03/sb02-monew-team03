package com.team03.monew.repository;

import com.team03.monew.entity.Subscription;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SubscriptionRepository extends JpaRepository<Subscription, UUID> {
  Optional<Subscription> findByUserIdAndInterestId(UUID userId, UUID interestId);
  void deleteByUserIdAndInterestId(UUID userId, UUID interestId);
  boolean existsByUserIdAndInterestId(UUID userId, UUID interestId);
}