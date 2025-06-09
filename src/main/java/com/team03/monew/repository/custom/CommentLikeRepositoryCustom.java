package com.team03.monew.repository.custom;

import com.team03.monew.entity.CommentLike;
import com.team03.monew.entity.User;
import java.util.List;

public interface CommentLikeRepositoryCustom {

    List<CommentLike> findTop10ByUserWithCommentAndNewsAndUser(User user);

}
