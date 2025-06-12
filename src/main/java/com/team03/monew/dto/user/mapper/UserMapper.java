package com.team03.monew.dto.user.mapper;

import com.team03.monew.dto.user.UserDto;
import com.team03.monew.dto.user.UserRegisterRequest;
import com.team03.monew.entity.User;
import org.springframework.stereotype.Component;

@Component
public class UserMapper {

    public User toUser(UserRegisterRequest request) {
        return new User(
            request.email(),
            request.nickname(),
            request.password()
        );
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
