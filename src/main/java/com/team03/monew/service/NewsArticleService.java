package com.team03.monew.service;

import com.team03.monew.dto.newsArticle.ArticleViewDto;
import com.team03.monew.dto.newsArticle.mapper.ArticleViewMapper;
import com.team03.monew.entity.ArticleView;
import com.team03.monew.entity.NewsArticle;
import com.team03.monew.exception.CustomException;
import com.team03.monew.exception.ErrorCode;
import com.team03.monew.exception.ErrorDetail;
import com.team03.monew.exception.ExceptionType;
import com.team03.monew.repository.ArticleViewRepository;
import com.team03.monew.repository.NewsArticleRepository;
import java.util.Optional;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class NewsArticleService {

    private final NewsArticleRepository newsRepository;
    private final ArticleViewRepository articleViewRepository;

    public ArticleViewDto saveArticleView(UUID articleId, UUID userId) {
        NewsArticle article = newsRepository.findByIdAndDeletedFalse(articleId)
            .orElseThrow(() ->{
                ErrorDetail detail = new ErrorDetail("UUID", "articleId", articleId.toString());
                return new CustomException(ErrorCode.RESOURCE_NOT_FOUND, detail, ExceptionType.NEWSARTICLE);
            });

        Optional<ArticleView> optional = articleViewRepository.findByArticleIdAndUserId(articleId, userId);
        if (optional.isPresent()) {
            return ArticleViewMapper.toDto(optional.get());
        }

        ArticleView view = articleViewRepository.save(new ArticleView(article, userId));
        article.increaseViewCount();
        return ArticleViewMapper.toDto(view);
    }
}
