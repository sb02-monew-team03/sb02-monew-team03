package com.team03.monew.repository;

import com.team03.monew.entity.Interest;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface InterestRepository extends JpaRepository<Interest, UUID>, InterestRepositoryCustom {
}