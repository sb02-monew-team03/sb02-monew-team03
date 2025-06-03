package com.team03.monew.repository;

import com.team03.monew.entity.Comment;
import com.team03.monew.repository.custom.CommentRepositoryCustom;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface CommentRepository extends JpaRepository<Comment, Long> ,
        CommentRepositoryCustom {

    @Query("SELECT c.likeCount FROM Comment c WHERE c.id = :commentId")
    Integer findLikeCountById(@Param("commentId") Long commentId);

    @Modifying
    @Query(value = "DELETE FROM comment_like WHERE comment_id = :commentId", nativeQuery = true)
    void deleteLikesByCommentId(@Param("commentId") long commentId);
}
