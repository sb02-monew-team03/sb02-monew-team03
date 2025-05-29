package com.team03.monew.repository;

import com.team03.monew.entity.CommentLike;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CommentLikeRepository extends JpaRepository<CommentLike, String> {
}
