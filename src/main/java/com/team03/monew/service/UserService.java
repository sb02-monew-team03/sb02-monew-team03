package com.team03.monew.service;

import com.team03.monew.dto.user.UserDto;
import com.team03.monew.dto.user.UserRegisterRequest;

public interface UserService {
    UserDto register(UserRegisterRequest request);
}
