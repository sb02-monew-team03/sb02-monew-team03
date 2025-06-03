package com.team03.monew.service;

import com.team03.monew.dto.user.UserDto;
import com.team03.monew.dto.user.UserLoginRequest;
import com.team03.monew.dto.user.UserRegisterRequest;
import com.team03.monew.dto.user.UserUpdateRequest;
import java.util.UUID;

public interface UserService {

    UserDto register(UserRegisterRequest request);

    UserDto updateNickname(UUID userId, UUID requesterId, UserUpdateRequest request);

    void deleteUser(UUID userId, UUID requesterId);

    void deleteUserHard(UUID userId, UUID requesterId);

    UserDto login(UserLoginRequest request);
}
