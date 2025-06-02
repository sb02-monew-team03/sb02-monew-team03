package com.team03.monew.repository;

import com.team03.monew.entity.Interest;
import com.team03.monew.repository.custom.InterestRepositoryCustom;
import java.util.List;
import com.team03.monew.repository.custom.InterestRepositoryCustom;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface InterestRepository extends JpaRepository<Interest, UUID>,
    InterestRepositoryCustom {
    @Query("SELECT i.name FROM Interest i")
    List<String> findAllNames();


}
