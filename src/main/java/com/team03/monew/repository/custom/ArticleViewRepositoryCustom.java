package com.team03.monew.repository.custom;

import com.team03.monew.entity.ArticleView;
import com.team03.monew.entity.User;
import java.util.List;

public interface ArticleViewRepositoryCustom {

    List<ArticleView> findTop10ByUserWithArticle(User user);

}
