package com.synclearn.security;

import com.synclearn.user.User;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.oauth2.core.user.OAuth2User;

import java.util.Collection;
import java.util.Map;

/**
 * CustomOAuth2User
 *
 * - Spring Security가 세션/인증에 사용하는 객체(OAuth2User)를 구현
 * - 내부적으로 domain.User를 그대로 들고 있을 필요는 없지만, 테스트/핵심 정보 접근을 위해 user정보를 프로퍼티로 보관
 *
 * Note: record로 구현하여 불변으로 유지
 */
public record CustomOAuth2User(
        User user,
        Map<String, Object> attributes
) implements OAuth2User {

    @Override
    public Map<String, Object> getAttributes() {
        return attributes;
    }

    // 권한을 필요하면 여기에 ROLE_USER 등을 반환
    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return java.util.List.of();
    }

    // Security에서 getName()으로 사용되는 대표 식별자(여기선 user.providerId)
    @Override
    public String getName() {
        return user.providerId();
    }
}