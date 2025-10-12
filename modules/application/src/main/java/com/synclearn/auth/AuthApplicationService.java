package com.synclearn.auth;

import com.synclearn.security.SecurityRepository;
import com.synclearn.user.User;
import com.synclearn.user.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class AuthApplicationService {

    private final UserRepository userRepository;
    private final SecurityRepository  securityRepository;

    public AuthApplicationService(UserRepository userRepository, SecurityRepository securityRepository) {
        this.userRepository = userRepository;
        this.securityRepository = securityRepository;
    }

    /**
     * OAuth로부터 받은 정보를 토대로 회원가입 또는 조회를 수행
     * - providerId: OAuth 로그인 일 때만 추가로 넣어 줄 컬럼
     */
    @Transactional
    public User registerFromOAuth(String providerId, String email, String name) {
        if (userRepository.existsByEmail(email)) {
            throw new IllegalArgumentException("이미 가입된 이메일 입니다.");
        }else if (userRepository.existsByProviderId(providerId)){
            throw new IllegalArgumentException("이미 소셜을 통해 가입된 ID 입니다.");
        }

        // 신규 가입: User 도메인 생성 후 엔티티로 변환 및 저장
        return userRepository.save(User.fromGithub(providerId, email, name));
    }

    /**
     * 로컬 회원가입
     * - 이메일 중복 체크
     * - 비밀번호 해시 처리(BCrypt)
     * - 도메인(User) 생성 및 저장
     */
    @Transactional
    public User registerFromLocal(String email, String rawPassword, String nickname) {
        if (userRepository.existsByEmail(email)) {
            throw new IllegalArgumentException("이미 가입된 이메일 입니다.");
        }
        User domainUser = User.fromLocal(email, securityRepository.pwEncrypt(rawPassword), nickname);
        return userRepository.save(domainUser);
    }
}