package com.team03.monew.repository.custom;

import com.team03.monew.entity.Comment;
import com.team03.monew.util.OrderBy;
import com.team03.monew.util.SortDirection;
import java.time.LocalDateTime;
import java.util.List;


public interface CommentRepositoryCustom {

    List<Comment> findByArticleWithCursorPaging(
            Long articleId,
            OrderBy orderBy,
            SortDirection direction,
            Long cursor,
            LocalDateTime after,
            int limit,
            Long requesterId
    );
}
