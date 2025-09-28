package com.synclearn.user;

import com.synclearn.user.enums.AuthProvider;
import com.synclearn.user.enums.UserRole;
import com.synclearn.user.enums.UserStatus;

import java.time.LocalDateTime;
import java.util.Objects;
import java.util.UUID;

/**
 * User 도메인 모델 (DB/JPA와 완전히 분리)
 * 불변성을 유지하고 생성 시 도메인 규칙 검증
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

    /** 비밀번호 없이 소셜 가입하는 경우를 대비해 Optional */
    public boolean hasPassword() {
        return password != null && !password.isBlank();
    }
}