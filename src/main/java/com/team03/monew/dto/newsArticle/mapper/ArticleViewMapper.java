package com.team03.monew.dto.newsArticle.mapper;

import com.team03.monew.dto.newsArticle.response.ArticleViewDto;
import com.team03.monew.entity.ArticleView;
import com.team03.monew.entity.NewsArticle;

public class ArticleViewMapper {

    public static ArticleViewDto toDto(ArticleView view) {
        NewsArticle a = view.getArticle();
        return new ArticleViewDto(
            view.getId(),
            view.getUser().getId(),
            view.getCreatedAt(),
            a.getId(),
            a.getSource(),
            a.getOriginalLink(),
            a.getTitle(),
            a.getDate(),
            a.getSummary(),
            a.getComments().size(),
            a.getViewCount()
        );
    }
}