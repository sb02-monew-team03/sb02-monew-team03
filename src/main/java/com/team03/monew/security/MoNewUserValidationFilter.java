package com.team03.monew.security;

import com.team03.monew.exception.CustomException;
import com.team03.monew.exception.ErrorCode;
import com.team03.monew.exception.ErrorDetail;
import com.team03.monew.exception.ExceptionType;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.UUID;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

@Slf4j
@Component
public class MoNewUserValidationFilter extends OncePerRequestFilter {

    private static final String HEADER_NAME = "Monew-Request-User-ID";

    @Override
    protected void doFilterInternal(HttpServletRequest request,
        HttpServletResponse response,
        FilterChain filterChain)
        throws ServletException, IOException {

        // 1. 인증 정보 가져오기
        // Authentication 객체 안에는 실제 인증된 사용자 정보 담겨 있음
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        log.info("[인가] 인증 정보 존재 여부: {}", authentication != null);
        log.info("[인가] 인증 여부: {}", authentication != null && authentication.isAuthenticated());

        // 인증되지 않은 요청은 다음 필터로 넘김
        // 비로그인 이거나 인증되지 않은 사용자 일때
        if (authentication == null || !authentication.isAuthenticated()) {
            log.warn("[인가] 인증되지 않은 요청. 그대로 진행");
            filterChain.doFilter(request, response);
            return;
        }

        // 2. 사용자 ID 꺼내기
        // Authentication 객체 안에 있는 principal를 꺼내서 CustomUserDetails 타입인지 검사
        Object principal = authentication.getPrincipal();
        if (!(principal instanceof CustomUserDetails userDetails)) {
            log.warn("[인가] 예상치 못한 Principal 타입. userDetails 아님.");
            filterChain.doFilter(request, response);
            return;
        }

        UUID sessionUserId = userDetails.getUserId();
        log.info("[인가] 세션 사용자 ID: {}", sessionUserId);


        // 3. 헤더에서 요청자 ID 꺼내기
        String headerValue = request.getHeader(HEADER_NAME);
        log.info("[인가] 요청 헤더 사용자 ID: {}", headerValue);
        if (headerValue == null) {
            log.error("[인가 실패] {} 헤더가 없음", HEADER_NAME);
            throw new CustomException(
                ErrorCode.UNAUTHORIZED,
                new ErrorDetail("HEADER", HEADER_NAME, null),
                ExceptionType.USER
            );
        }

        try {
            UUID requestUserId = UUID.fromString(headerValue);

            if (!sessionUserId.equals(requestUserId)) {
                log.error("[인가 실패] 세션 ID와 요청 헤더 ID 불일치: {} vs {}", sessionUserId, requestUserId);
                throw new CustomException(
                    ErrorCode.FORBIDDEN,
                    new ErrorDetail("USER", "userId", requestUserId.toString()),
                    ExceptionType.USER
                );
            }

        } catch (IllegalArgumentException e) {
            log.error("[인가 실패] UUID 파싱 실패: {}", headerValue);
            throw new CustomException(
                ErrorCode.INVALID_INPUT_VALUE,
                new ErrorDetail("UUID", HEADER_NAME, headerValue),
                ExceptionType.USER
            );
        }

        // 4. 통과 -> 다음 필터로 넘김
        log.info("[인가 성공] 요청 통과");
        filterChain.doFilter(request, response);
    }

}
