package com.team03.monew.repository.custom;

import com.team03.monew.entity.Comment;
import com.team03.monew.repository.OrderBy;
import com.team03.monew.repository.SortDirection;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

public interface CommentCustomRepository {

    List<Comment> findByArticleWithCursorPaging(
            UUID articleId,
            OrderBy orderBy,
            SortDirection direction,
            UUID cursor,
            LocalDateTime after,
            int limit,
            UUID requesterId
    );
}
