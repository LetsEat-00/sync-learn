package com.synclearn.security;

import com.synclearn.security.jwt.JwtTokenProvider;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;

/**
 * OAuth2SuccessHandler
 *
 * - OAuth2 인증이 완료된 직후에 호출되는 핸들러
 * - 여기에서 JWT를 발급하고 쿠키에 실어 응답에 추가
 * - 초대 state 처리나 추가 리다이렉트 로직도 이곳에서 수행 가능
 */
@Component
public class OAuth2SuccessHandler implements AuthenticationSuccessHandler {

    private final JwtTokenProvider jwtTokenProvider;

    public OAuth2SuccessHandler(JwtTokenProvider jwtTokenProvider) {
        this.jwtTokenProvider = jwtTokenProvider;
    }

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response,
                                        Authentication authentication) throws IOException {
        // Authentication의 principal은 우리가 반환한 CustomOAuth2User
        Object principal = authentication.getPrincipal();
        if (!(principal instanceof CustomOAuth2User cUser)) {
            // 안전상: principal 타입이 다르면 루트로 리다이렉트
            response.sendRedirect("/");
            return;
        }

        // domain.User로부터 ID/이메일 등 정보 획득
        String userId = cUser.user().id().toString(); // UUID -> 문자열

        // JWT 발급 (access token)
        String accessToken = jwtTokenProvider.createAccessToken(userId);

        // 쿠키에 토큰 세팅 (실무: Secure, SameSite, 도메인 설정 필요)
        Cookie accessCookie = new Cookie("ACCESS_TOKEN", accessToken);
        accessCookie.setHttpOnly(true);
        accessCookie.setPath("/");
        // 예시: 1시간 만료
        accessCookie.setMaxAge(60 * 60);
        response.addCookie(accessCookie);

        // 리다이렉트: 프론트 URL로 리다이렉트하거나, 응답 바디로 토큰을 내려줄 수 있음
        response.sendRedirect("/after-login"); // 프론트 경로로 변경
    }
}