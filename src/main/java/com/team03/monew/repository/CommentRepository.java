package com.team03.monew.repository;

import com.team03.monew.entity.Comment;
import com.team03.monew.entity.User;
import com.team03.monew.repository.custom.CommentRepositoryCustom;
import java.util.List;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CommentRepository extends JpaRepository<Comment, UUID>,
    CommentRepositoryCustom {

    List<Comment> findTop10ByUserOrderByCreatedAtDesc(User user);

}
