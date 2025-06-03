package com.team03.monew.service;

import com.team03.monew.dto.user.UserDto;
import com.team03.monew.dto.user.UserLoginRequest;
import com.team03.monew.dto.user.mapper.UserMapper;
import com.team03.monew.dto.user.UserRegisterRequest;
import com.team03.monew.dto.user.UserUpdateRequest;
import com.team03.monew.entity.User;
import com.team03.monew.exception.CustomException;
import com.team03.monew.exception.ErrorCode;
import com.team03.monew.exception.ErrorDetail;
import com.team03.monew.exception.ExceptionType;
import com.team03.monew.repository.ActivityRepository;
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
    private final ActivityRepository activityRepository;

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
        User user = userRepository.findByIdAndDeletedFalse(userId).orElseThrow(() -> {
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

    @Override
    public void deleteUser(UUID userId, UUID requesterId) {
        User user = userRepository.findByIdAndDeletedFalse(userId).orElseThrow(() -> {
            ErrorDetail detail = new ErrorDetail("UUID", "userId", userId.toString());
            return new CustomException(ErrorCode.RESOURCE_NOT_FOUND, detail, ExceptionType.USER);
        });

        if (!user.getId().equals(requesterId)) {
            ErrorDetail detail = new ErrorDetail("USER", "userId", requesterId.toString());
            throw new CustomException(ErrorCode.FORBIDDEN, detail, ExceptionType.USER);
        }

        // 사용자 계정을 논리적으로 삭제 처리
        user.markAsDeleted();
    }

    @Override
    public void deleteUserHard(UUID userId, UUID requesterId) {
        User user = userRepository.findById(userId).orElseThrow(() -> {
            ErrorDetail detail = new ErrorDetail("UUID", "userId", userId.toString());
            return new CustomException(ErrorCode.RESOURCE_NOT_FOUND, detail, ExceptionType.USER);
        });

        if (!user.getId().equals(requesterId)) {
            ErrorDetail detail = new ErrorDetail("USER", "userId", requesterId.toString());
            throw new CustomException(ErrorCode.FORBIDDEN, detail, ExceptionType.USER);
        }

        // 활동 내역은 수동 삭제 (1:1 관계이므로 cascade 작동 x)
        activityRepository.deleteByUser(user);

        // 사용자 계정을 물리적으로 삭제 처리 (연관 요소 cascade 로 자동 삭제)
        userRepository.delete(user);
    }

    @Override
    public UserDto login(UserLoginRequest request) {
        User user = userRepository.findByEmail(request.email()).orElseThrow(() -> {
            ErrorDetail detail = new ErrorDetail("EMAIL", "email", request.email());
            return new CustomException(ErrorCode.RESOURCE_NOT_FOUND, detail, ExceptionType.USER);
        });

        if (!user.getPassword().equals(request.password())) {
            ErrorDetail detail = new ErrorDetail("PASSWORD", "password", request.password());
            throw new CustomException(ErrorCode.UNAUTHORIZED, detail, ExceptionType.USER);
        }

        return userMapper.toDto(user);
    }

}
