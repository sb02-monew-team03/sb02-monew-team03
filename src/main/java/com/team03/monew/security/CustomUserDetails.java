package com.team03.monew.security;

import com.team03.monew.entity.User;
import java.util.Collection;
import java.util.Collections;
import java.util.UUID;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

@Getter
@RequiredArgsConstructor
public class CustomUserDetails implements UserDetails {

    private final User user;

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return Collections.emptyList(); // 지금은 권한 관리 안하므로 빈 리스트 반환
    }

    @Override
    public String getUsername() {
        return user.getEmail();  // 이메일로 로그인 하므로 email임
    }

    @Override
    public String getPassword() {
        return user.getPassword();
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return !user.isDeleted();
    }

    // 사용자 ID 따로 꺼내쓰고 싶을 때 사용
    public UUID getUserId() {
        return user.getId();
    }

    public String getNickname() {
        return user.getNickname();
    }
}
