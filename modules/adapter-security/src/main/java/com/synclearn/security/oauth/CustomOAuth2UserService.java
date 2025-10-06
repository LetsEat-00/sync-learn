package com.synclearn.security.oauth;


import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.synclearn.auth.AuthApplicationService;
import com.synclearn.security.CustomOAuth2User;
import com.synclearn.user.User;
import org.springframework.security.oauth2.client.userinfo.*;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.Map;

/**
 * CustomOAuth2UserService
 *
 * - Spring의 DefaultOAuth2UserService를 delegate로 사용하여 GitHub에서 user info를 조회
 * - GitHub 경우 email이 null로 올 수 있으므로, 필요한 경우 /user/emails API를 추가로 호출하여 이메일을 확보
 * - 확보한 정보를 application.AuthApplicationService에 전달하여 "회원가입 혹은 조회"를 수행
 * - 최종적으로 Security가 사용할 OAuth2User(여기선 CustomOAuth2User)를 반환
 */
@Service
public class CustomOAuth2UserService implements OAuth2UserService<OAuth2UserRequest, OAuth2User> {

    private final DefaultOAuth2UserService delegate = new DefaultOAuth2UserService();
    private final AuthApplicationService authService; // application 계층의 회원가입/조회 유스케이스
    private final RestTemplate restTemplate = new RestTemplate();
    private final ObjectMapper mapper = new ObjectMapper();

    public CustomOAuth2UserService(AuthApplicationService authService) {
        this.authService = authService;
    }

    @Override
    public OAuth2User loadUser(OAuth2UserRequest userRequest) {
        // 기본 동작으로 GitHub 사용자 정보(fetch)
        OAuth2User oauth2User = delegate.loadUser(userRequest);

        // GitHub가 준 속성들 (id, login, name, email 등) // avatar_url란 것도 있던데 나중에 확인해 볼 예정
        Map<String, Object> attrs = oauth2User.getAttributes();
        String providerId = String.valueOf(attrs.get("id")); // GitHub user id (숫자)
        String name = (String) attrs.getOrDefault("name", attrs.get("login"));

        // 이메일: GitHub가 직접 주지 않는 경우가 있어서 보완 로직을 둠
        String email = (String) attrs.get("email");
        if (email == null || email.isBlank()) {
            // userRequest.getAccessToken()으로 /user/emails 호출
            try {
                String token = userRequest.getAccessToken().getTokenValue();
                var response = restTemplate.getForObject(
                        "https://api.github.com/user/emails?access_token=" + token,
                        String.class
                );
                if (response != null) {
                    JsonNode arr = mapper.readTree(response);
                    // primary && verified 인 이메일을 우선적으로 찾음
                    for (JsonNode n : arr) {
                        boolean primary = n.has("primary") && n.get("primary").asBoolean(false);
                        boolean verified = n.has("verified") && n.get("verified").asBoolean(false);
                        if (primary && verified && n.has("email")) {
                            email = n.get("email").asText();
                            break;
                        }
                    }
                    // 없으면 그냥 첫번째 이메일 사용(테스트/개발 편의)
                    if ((email == null || email.isBlank()) && arr.size() > 0) {
                        email = arr.get(0).get("email").asText();
                    }
                }
            } catch (Exception ex) {
                // 이메일 가져오기 실패 시 로그 남기고 진행 (운영에서는 에러/재시도 정책 필요)
                email = null;
            }
        }

        // application 계층에 넘김 providerId, email, name 로 회원가입/조회 처리
        User user = authService.registerFromOAuth(providerId, email, name);

        // SecurityContext에 들어갈 OAuth2User 반환.
        // domain.User (record)은 불변이므로, Security용으로 필요한 필드만 복사한 CustomOAuth2User를 반환
        return new CustomOAuth2User(user, attrs);
    }
}