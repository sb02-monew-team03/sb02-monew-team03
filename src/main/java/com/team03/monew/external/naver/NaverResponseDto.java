package com.team03.monew.external.naver;

import java.util.List;
import lombok.Data;

@Data
public class NaverResponseDto {
    private List<NaverNewsItem> items;
}