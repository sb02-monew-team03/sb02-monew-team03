package com.team03.monew.security;

import com.team03.monew.entity.User;
import com.team03.monew.exception.CustomException;
import com.team03.monew.exception.ErrorCode;
import com.team03.monew.exception.ErrorDetail;
import com.team03.monew.exception.ExceptionType;
import com.team03.monew.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class CustomUserDetailsService implements UserDetailsService {

    private final UserRepository userRepository;

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        log.info("[인증] 로그인 시도 - 이메일: {}", email);


        User user = userRepository.findByEmail(email).orElseThrow(() -> {
            ErrorDetail detail = new ErrorDetail("EMAIL", "email", email);
            return new CustomException(ErrorCode.RESOURCE_NOT_FOUND, detail, ExceptionType.USER);
        });

        log.info("[인증 성공] 사용자 로딩 완료 - 사용자 ID: {}", user.getId());
        return new CustomUserDetails(user);
    }
}
