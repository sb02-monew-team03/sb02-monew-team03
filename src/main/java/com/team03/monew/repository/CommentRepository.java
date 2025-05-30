package com.team03.monew.repository;

import com.team03.monew.entity.Comment;
import com.team03.monew.repository.Custom.CommentCustomRepository;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CommentRepository extends JpaRepository<Comment, UUID> , CommentCustomRepository{
}
