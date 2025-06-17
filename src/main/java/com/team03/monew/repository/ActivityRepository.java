package com.team03.monew.repository;

import com.team03.monew.entity.Activity;
import java.util.UUID;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ActivityRepository extends MongoRepository<Activity, UUID> {

}
