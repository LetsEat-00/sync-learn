package com.synclearn.user;

import com.synclearn.user.enums.AuthProvider;
import com.synclearn.user.enums.UserRole;
import com.synclearn.user.enums.UserStatus;

import java.time.LocalDateTime;
import java.util.Objects;
import java.util.UUID;

/**
 * SyncLearn 서비스의 사용자 도메인 모델.
 * <p>
 * 영속성 계층과 분리된 불변 객체로, 생성 시 필수 값 검증을 수행한다.
 * </p>
 *
 * @param id 사용자를 식별하는 UUID (신규 생성 시 null 허용)
 * @param email 로그인에 사용되는 이메일 주소
 * @param nickname 서비스에 노출되는 닉네임
 * @param password 암호화된 비밀번호 또는 소셜 가입 시 null
 * @param provider 가입 경로를 나타내는 인증 제공자
 * @param role 시스템 내 권한 등급
 * @param status 현재 계정 상태
 * @param createdAt 계정 생성 시각
 */
public record User(
        UUID id,
        String email,
        String nickname,
        String password,
        AuthProvider provider,
        UserRole role,
        UserStatus status,
        LocalDateTime createdAt
) {
    // Compact Constructor: record에서 필드 검증 로직 가능
    public User {
        Objects.requireNonNull(email, "이메일은 필수입니다.");
        Objects.requireNonNull(nickname, "사용자 이름은 필수입니다.");
        Objects.requireNonNull(provider, "가입 제공자는 필수입니다.");
        Objects.requireNonNull(role, "역할은 필수입니다.");
        Objects.requireNonNull(status, "상태는 필수입니다.");

        if (!email.matches("^[\\w._%+-]+@[\\w.-]+\\.[a-zA-Z]{2,}$")) {
            throw new IllegalArgumentException("이메일 형식이 올바르지 않습니다.");
        }
        if (nickname.isBlank()) {
            throw new IllegalArgumentException("사용자 이름은 공백일 수 없습니다.");
        }
    }

    /**
     * 비밀번호 없이 소셜 가입하는 경우를 대비해 비밀번호 보유 여부를 확인한다.
     *
     * @return 비밀번호가 존재하고 공백이 아닌 경우 true, 그 외에는 false
     */
    public boolean hasPassword() {
        return password != null && !password.isBlank();
    }
}
