package com.synclearn.user;

import com.synclearn.user.enums.AuthProvider;
import com.synclearn.user.enums.UserRole;
import com.synclearn.user.enums.UserStatus;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

/**
 * {@link User} 레코드의 유효성 검증 로직과 헬퍼 메서드를 테스트한다.
 */
class UserTest {

    private static final LocalDateTime NOW = LocalDateTime.of(2024, 1, 1, 0, 0);

    /**
     * 필수 값이 모두 채워졌을 때 정상적으로 사용자 인스턴스를 생성하는지 확인한다.
     */
    @Test
    void createsUserWhenMandatoryFieldsAreValid() {
        UUID id = UUID.fromString("123e4567-e89b-12d3-a456-426614174000");
        User user = new User(
                id,
                "user@example.com",
                "nickname",
                "encrypted",
                AuthProvider.LOCAL,
                UserRole.USER,
                UserStatus.ACTIVE,
                NOW
        );

        assertAll(
                () -> assertEquals(id, user.id()),
                () -> assertEquals("user@example.com", user.email()),
                () -> assertEquals("nickname", user.nickname()),
                () -> assertEquals("encrypted", user.password()),
                () -> assertEquals(AuthProvider.LOCAL, user.provider()),
                () -> assertEquals(UserRole.USER, user.role()),
                () -> assertEquals(UserStatus.ACTIVE, user.status()),
                () -> assertEquals(NOW, user.createdAt())
        );
    }

    /**
     * 이메일이 null이면 필수 값 검증이 동작하여 예외를 던지는지 검증한다.
     */
    @Test
    void throwsWhenEmailIsNull() {
        NullPointerException exception = assertThrows(NullPointerException.class, () ->
                new User(
                        UUID.randomUUID(),
                        null,
                        "nickname",
                        "encrypted",
                        AuthProvider.LOCAL,
                        UserRole.USER,
                        UserStatus.ACTIVE,
                        NOW
                )
        );

        assertEquals("이메일은 필수입니다.", exception.getMessage());
    }

    /**
     * 이메일 형식이 잘못되었을 때 IllegalArgumentException이 발생하는지 확인한다.
     */
    @Test
    void throwsWhenEmailFormatIsInvalid() {
        assertThrows(IllegalArgumentException.class, () ->
                new User(
                        UUID.randomUUID(),
                        "invalid-email",
                        "nickname",
                        "encrypted",
                        AuthProvider.LOCAL,
                        UserRole.USER,
                        UserStatus.ACTIVE,
                        NOW
                )
        );
    }

    /**
     * 닉네임이 공백일 경우 예외가 발생해 도메인 규칙을 지키는지 검증한다.
     */
    @Test
    void throwsWhenNicknameIsBlank() {
        assertThrows(IllegalArgumentException.class, () ->
                new User(
                        UUID.randomUUID(),
                        "user@example.com",
                        " ",
                        "encrypted",
                        AuthProvider.LOCAL,
                        UserRole.USER,
                        UserStatus.ACTIVE,
                        NOW
                )
        );
    }

    /**
     * 비밀번호가 null이면 hasPassword가 false를 반환하는지 확인한다.
     */
    @Test
    void hasPasswordReturnsFalseWhenPasswordIsNull() {
        User user = new User(
                null,
                "user@example.com",
                "nickname",
                null,
                AuthProvider.LOCAL,
                UserRole.USER,
                UserStatus.ACTIVE,
                NOW
        );

        assertFalse(user.hasPassword());
    }

    /**
     * 비밀번호가 공백 문자열이면 hasPassword가 false인지 검증한다.
     */
    @Test
    void hasPasswordReturnsFalseWhenPasswordIsBlank() {
        User user = new User(
                null,
                "user@example.com",
                "nickname",
                " ",
                AuthProvider.LOCAL,
                UserRole.USER,
                UserStatus.ACTIVE,
                NOW
        );

        assertFalse(user.hasPassword());
    }

    /**
     * 값이 있는 비밀번호가 주어졌을 때 hasPassword가 true를 반환하는지 확인한다.
     */
    @Test
    void hasPasswordReturnsTrueWhenPasswordExists() {
        User user = new User(
                null,
                "user@example.com",
                "nickname",
                "encrypted",
                AuthProvider.LOCAL,
                UserRole.USER,
                UserStatus.ACTIVE,
                NOW
        );

        assertTrue(user.hasPassword());
    }
}
