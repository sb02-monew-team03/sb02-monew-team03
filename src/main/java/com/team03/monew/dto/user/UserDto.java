package com.team03.monew.dto.user;

import java.time.LocalDateTime;

public record UserDto(
    Long id,
    String email,
    String nickname,
    LocalDateTime createdAt
) {

}
