package com.team03.monew.config;

import com.team03.monew.security.CustomUserDetailsService;
import com.team03.monew.security.MoNewUserValidationFilter;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.filter.CorsFilter;

@Configuration
@EnableWebSecurity  //Spring Security를 활성화하겠다는 의미
@RequiredArgsConstructor
public class SecurityConfig {

    private final CustomUserDetailsService customUserDetailsService;

    // Spring Security 에서 보안 설정 정의
    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
            .csrf(csrf -> csrf.disable()) // CSRF 보호는 REST API에선 필요 없으므로 비활성화
            .formLogin(form -> form.disable()) // 기본 HTML 로그인 폼 비활성화
            .httpBasic(basic -> basic.disable()) // HTTP Basic 인증 팝업 비활성화

            // 인증 허용, 차단 경로 설정
            .authorizeHttpRequests(auth -> auth
                .requestMatchers("/", "/index.html", "/favicon.ico", "/assets/**").permitAll()
                .requestMatchers("/api/users/login", "/api/users").permitAll() // 로그인, 회원가입은 누구나 가능
                .requestMatchers("/actuator/**").permitAll() // 액츄에이터 허용
                .anyRequest().authenticated()   // 그 외는 모두 로그인 필요(인증 필요)
            )

            // 세션 관련 (로그인 성공 시 세션 자동 생성)
            .sessionManagement(session -> session
                .sessionCreationPolicy(SessionCreationPolicy.IF_REQUIRED)
            )

            .securityContext(securityContext -> securityContext
                .requireExplicitSave(false)
            )

            // 커스텀 필터 등록
            // UsernamePasswordAuthenticationFilter 다음에 MoNewUserValidationFilter를 실행하도록 설정
            .addFilterAfter(new MoNewUserValidationFilter(),
                UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }

    // 수동 인증 시 사용할 AuthenticationManager Bean 등록
    // formLogin()을 사용하지 않기 때문에 -> 인증 직접 처리 위해 Bean 등록
    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration config)
        throws Exception {
        return config.getAuthenticationManager();
    }

    @Bean
    public CorsFilter corsFilter() {
        CorsConfiguration config = new CorsConfiguration();
        config.setAllowCredentials(true); // 쿠키 포함 요청 허용
        config.setAllowedOriginPatterns(List.of("*"));
        config.setAllowedMethods(List.of("GET", "POST", "PATCH", "DELETE", "OPTIONS"));
        config.setAllowedHeaders(List.of("*"));
        config.setExposedHeaders(List.of("Authorization", "Content-Type", "Monew-Request-User-ID"));

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", config);
        return new CorsFilter(source);
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}
