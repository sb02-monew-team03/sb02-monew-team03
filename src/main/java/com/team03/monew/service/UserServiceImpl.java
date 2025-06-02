package com.team03.monew.service;

import com.team03.monew.dto.user.UserDto;
import com.team03.monew.dto.user.UserMapper;
import com.team03.monew.dto.user.UserRegisterRequest;
import com.team03.monew.dto.user.UserUpdateRequest;
import com.team03.monew.entity.User;
import com.team03.monew.exception.CustomException;
import com.team03.monew.exception.ErrorCode;
import com.team03.monew.exception.ErrorDetail;
import com.team03.monew.exception.ExceptionType;
import com.team03.monew.repository.UserRepository;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
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

    @Override
    public UserDto updateNickname(UUID userId, UUID requesterId, UserUpdateRequest request) {
        User user = userRepository.findById(userId).orElseThrow(() -> {
            ErrorDetail detail = new ErrorDetail("UUID", "userId", userId.toString());
            return new CustomException(ErrorCode.RESOURCE_NOT_FOUND, detail, ExceptionType.USER);
        });

        // 수정대상의 ID 와 요청을 보낸 사용자의 ID 가 같아야 수정이 가능
        if (!user.getId().equals(requesterId)) {
            ErrorDetail detail = new ErrorDetail("USER", "userId", requesterId.toString());
            throw new CustomException(ErrorCode.FORBIDDEN, detail, ExceptionType.USER);
        }

        user.updateNickname(request.nickname());
        return userMapper.toDto(user);
    }
}
