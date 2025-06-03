package com.team03.monew.dto.user;

import com.team03.monew.entity.User;
import org.springframework.stereotype.Component;

@Component
public class UserMapper {

    public User toUser(UserRegisterRequest request) {
        return User.builder()
                .email(request.email())
                .nickname(request.nickname())
                .password(request.password())
                .build();
    }

    public UserDto toDto(User user) {
        return new UserDto(
            user.getId(),
            user.getEmail(),
            user.getNickname(),
            user.getCreatedAt()
        );
    }

}
