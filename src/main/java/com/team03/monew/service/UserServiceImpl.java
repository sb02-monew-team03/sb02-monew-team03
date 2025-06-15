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
//import com.team03.monew.repository.ActivityRepository;
import com.team03.monew.repository.UserRepository;
import com.team03.monew.security.CustomUserDetails;
import jakarta.servlet.http.HttpServletRequest;
import java.security.Security;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
@RequiredArgsConstructor
@Slf4j
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final UserMapper userMapper;
    //    private final ActivityRepository activityRepository;
    private final UserDetailsService userDetailsService;
    private final PasswordEncoder passwordEncoder;

    @Override
    public UserDto register(UserRegisterRequest request) {
        // 평문 비밀번호를 해시된 문자열로 변환
        String hashedPassword = passwordEncoder.encode(request.password());
        log.info("[회원가입] 해싱된 비밀번호: {}", hashedPassword);

        if (userRepository.existsByEmail(request.email())) {
            ErrorDetail detail = new ErrorDetail("Email", "email", request.email());
            throw new CustomException(ErrorCode.CONFLICT, detail, ExceptionType.USER);
        }

        // 비밀번호 해싱 처리
        UserRegisterRequest secureRequest = new UserRegisterRequest(
            request.email(),
            request.nickname(),
            hashedPassword
        );

        User user = userMapper.toUser(secureRequest);
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
//        activityRepository.deleteByUser(user);  -> 추후 MongoDB 사용시 교체, PostgreSQL에서는 Activity 테이블이 존재하지 않아서 살려둘 필요X

        // 사용자 계정을 물리적으로 삭제 처리 (연관 요소 cascade 로 자동 삭제)
        userRepository.delete(user);
    }

    @Override
    public UserDto login(UserLoginRequest request, HttpServletRequest httpRequest) {
        // 사용자 조회
        CustomUserDetails userDetails = (CustomUserDetails)
            userDetailsService.loadUserByUsername(request.email());

        // 사용자가 입력한 비밀번호(평문)를 DB에 저장된 해시값에 내장된 salt/cost 정보를 사용해서 같은 방식으로 다시 해싱 하고,
        // 그 결과가 DB에 저장된 해시값과 일치하는지 확인
        if (!passwordEncoder.matches(request.password(), userDetails.getPassword())) {
            ErrorDetail detail = new ErrorDetail("PASSWORD", "password", request.password());
            throw new CustomException(ErrorCode.UNAUTHORIZED, detail, ExceptionType.USER);
        }

        // 로그인한 사용자 정보를 담은 인증 객체 생성 (토큰 생성)
        UsernamePasswordAuthenticationToken authentication =
            new UsernamePasswordAuthenticationToken(userDetails, null,
                userDetails.getAuthorities());

        // 인증 객체를 SecurityContext에 저장 -> 세션 생성을 통해 로그인 상태 유지
        SecurityContextHolder.getContext().setAuthentication(authentication);
        log.info("[인증] SecurityContext 저장 완료");

        // 세션을 생성해줘야 SecurityContextPersistenceFilter가 저장함
        // 서버가 세션 생성 + JSESSIONID 부여
        // JSESSIONID 가 클라이언트 브라우저에 저장됨 + 이후 모든 요청의 쿠키에 JSESSIONID 를 포함시켜 요청하게 됨.
        // 서버는 이 JSESSIONID 를 보고 기존 세션을 꺼내게 됨.
        httpRequest.getSession(true);
        log.info("[인증] 세션 생성 완료");

        // 응답
        return userMapper.toDto(userDetails.getUser());
    }

}
