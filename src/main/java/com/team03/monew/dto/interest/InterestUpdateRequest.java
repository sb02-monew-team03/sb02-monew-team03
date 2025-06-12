package com.team03.monew.dto.interest;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;
import java.util.List;

public record InterestUpdateRequest(

    @NotEmpty(message = "키워드 목록은 최소 1개 이상이어야 합니다.")
    @Size(min = 1, max = 10, message = "키워드는 1개 이상 10개 이하로 입력해주세요.")
    List<@NotBlank(message = "키워드는 빈 문자열일 수 없습니다.") String> keywords

) {}
