package com.team03.monew.repository;

import com.team03.monew.entity.Comment;
import com.team03.monew.entity.CommentLike;
import com.team03.monew.entity.User;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CommentLikeRepository extends JpaRepository<CommentLike, Long> {
    boolean existsByCommentAndUser(Comment comment, User user);

    Optional<CommentLike> findByCommentAndUser(Comment comment, User user);
}
