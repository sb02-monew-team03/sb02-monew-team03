package com.team03.monew.dto.newsArticle.response;

import java.util.List;

// 뉴스 출처 목록 응답용
public record SourcesResponseDto (
    List<String> sources
) {

}
