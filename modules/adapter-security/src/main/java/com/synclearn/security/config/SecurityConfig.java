package com.synclearn.security.config;

import com.synclearn.security.OAuth2SuccessHandler;
import com.synclearn.security.oauth.CustomOAuth2UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.web.SecurityFilterChain;

/**
 * SecurityConfig
 *
 * - OAuth2 로그인 엔드포인트를 활성화하고,
 * - OAuth 인증 후 사용자 정보를 처리할 CustomOAuth2UserService를 연결,
 * - 인증 성공 시 추가 작업(예: JWT 발급, 초대 처리)을 담당하는 OAuth2SuccessHandler를 연결.
 *
 * 보통 이 클래스는 adapter-security 모듈에 두고, 스프링 부트 애플리케이션이
 * "com.synclearn" 패키지를 스캔하면 빈으로 등록됩니다.
 */
@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    private final CustomOAuth2UserService customOAuth2UserService;
    private final OAuth2SuccessHandler oAuth2SuccessHandler;

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        // 기본적인 보안/허용 설정
        http
                // CSRF: 프론트가 없는 API 테스트용이면 비활성화(운영 시엔 적절히 설정 필요)
                .csrf(csrf -> csrf.disable())

                // URL 권한 설정: OAuth 관련 엔드포인트와 public API는 허용
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/", "/login", "/oauth2/**").permitAll()
                        .anyRequest().authenticated()
                )

                // OAuth2 Login 설정
                .oauth2Login(oauth2 -> oauth2
                        // userInfoEndpoint에서 CustomOAuth2UserService를 사용하도록 설정
                        .userInfoEndpoint(userInfo -> userInfo
                                .userService(customOAuth2UserService)
                        )
                        // 인증 성공 시 호출될 핸들러 (JWT 발급, 쿠키 세팅, 리다이렉트 등)
                        .successHandler(oAuth2SuccessHandler)
                );

        // (테스트 편의) H2 콘솔 사용하는 경우 frameOptions 비활성화
        http.headers(headers -> headers.frameOptions(frame -> frame.disable()));

        return http.build();
    }
}