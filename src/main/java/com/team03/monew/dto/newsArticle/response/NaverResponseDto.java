package com.team03.monew.dto.newsArticle.response;

import com.team03.monew.external.naver.NaverNewsItem;
import java.util.List;
import lombok.Data;

@Data
public class NaverResponseDto {
    private List<NaverNewsItem> items;
}