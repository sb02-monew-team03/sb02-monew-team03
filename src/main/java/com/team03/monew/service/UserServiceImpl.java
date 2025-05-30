package com.team03.monew.service;

import com.team03.monew.dto.user.UserDto;
import com.team03.monew.dto.user.UserMapper;
import com.team03.monew.dto.user.UserRegisterRequest;
import com.team03.monew.entity.User;
import com.team03.monew.exception.CustomException;
import com.team03.monew.exception.ErrorCode;
import com.team03.monew.exception.ErrorDetail;
import com.team03.monew.exception.ExceptionType;
import com.team03.monew.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final UserMapper userMapper;

    @Override
    public UserDto register(UserRegisterRequest request) {
        if (userRepository.existsByEmail(request.email())) {
            ErrorDetail detail = new ErrorDetail("Email", "email", request.email());
            throw new CustomException(ErrorCode.CONFLICT, detail, ExceptionType.USER);
        }

        User user = userMapper.toUser(request);
        User saved = userRepository.save(user);
        return userMapper.toDto(saved);
    }
}
