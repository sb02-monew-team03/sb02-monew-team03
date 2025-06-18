package com.team03.monew.repository;

import com.team03.monew.entity.Interest;
import com.team03.monew.entity.Subscription;
import com.team03.monew.entity.User;
import com.team03.monew.repository.custom.SubscriptionRepositoryCustom;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface SubscriptionRepository extends JpaRepository<Subscription, UUID>,
    SubscriptionRepositoryCustom {

    @Query("""
    SELECT s FROM Subscription s 
    WHERE s.user.id = :userId AND s.interest.id = :interestId
""")
    Optional<Subscription> findByUserAndInterest(@Param("userId") UUID userId, @Param("interestId") UUID interestId);


    void deleteByUserIdAndInterestId(UUID userId, UUID interestId);

    boolean existsByUserIdAndInterestId(UUID userId, UUID interestId);

    @Query("SELECT s.user FROM Subscription s WHERE s.interest = :interest")
    List<User> findAllByInterest(@Param("interest") Interest interest);

    List<Subscription> findByUserOrderByCreatedAtDesc(User user);

    void deleteByInterest(Interest interest);

    List<Subscription> findSubscriptionsByInterest(Interest interest);
}
