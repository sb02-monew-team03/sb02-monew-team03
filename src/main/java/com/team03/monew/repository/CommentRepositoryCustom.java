package com.team03.monew.repository;

import com.team03.monew.entity.Comment;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CommentRepositoryCustom extends JpaRepository<Comment, UUID> ,
        com.team03.monew.repository.custom.CommentRepositoryCustom {
}
