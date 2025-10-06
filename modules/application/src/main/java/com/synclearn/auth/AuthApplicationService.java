package com.synclearn.auth;

import com.synclearn.user.User;
import com.synclearn.user.UserEntity;
import com.synclearn.user.UserJpaRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class AuthApplicationService {

    private final UserJpaRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public AuthApplicationService(UserJpaRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    /**
     * OAuth로부터 받은 정보를 토대로 회원가입 또는 조회를 수행
     * - providerId: OAuth 로그인 일 때만 추가로 넣어 줄 컬럼
     */
    @Transactional
    public User registerFromOAuth(String providerId, String email, String name) {
        // providerId 기준으로 기존 유저 확인
        return userRepository.findByProviderId(providerId)
                .map(UserEntity::toDomain)
                .orElseGet(() -> {
                    // 신규 가입: User 도메인 생성 후 엔티티로 변환 및 저장
                    User domainUser = User.fromGithub(providerId, email, name);
                    UserEntity saved = userRepository.save(UserEntity.fromDomain(domainUser));
                    return saved.toDomain();
                });
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
        String hashed = passwordEncoder.encode(rawPassword);
        User domainUser = User.fromLocal(email, hashed, nickname);
        return userRepository.save(UserEntity.fromDomain(domainUser)).toDomain();
    }
}