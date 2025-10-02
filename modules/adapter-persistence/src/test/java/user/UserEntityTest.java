package user;

import com.synclearn.user.User;
import com.synclearn.user.UserEntity;
import com.synclearn.user.enums.AuthProvider;
import com.synclearn.user.enums.UserRole;
import com.synclearn.user.enums.UserStatus;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

/**
 * {@link UserEntity} 변환 로직을 단위 테스트한다.
 */
class UserEntityTest {

    /**
     * 도메인 모델의 모든 필드가 엔티티로 정확히 복사되는지 검증한다.
     */
    @Test
    void fromDomainShouldCopyAllFields() {
        LocalDateTime createdAt = LocalDateTime.of(2024, 1, 2, 3, 4, 5);
        UUID userId = UUID.fromString("123e4567-e89b-12d3-a456-426614174000");
        User domainUser = new User(
                userId,
                "user@example.com",
                "nickname",
                "secret",
                AuthProvider.LOCAL,
                UserRole.USER,
                UserStatus.ACTIVE,
                createdAt
        );

        UserEntity entity = UserEntity.fromDomain(domainUser);
        User converted = entity.toDomain();

        assertEquals(domainUser, converted);
    }

    /**
     * 도메인 모델의 식별자가 null인 경우에도 엔티티 생성이 가능함을 확인한다.
     */
    @Test
    void fromDomainShouldAllowNullId() {
        LocalDateTime createdAt = LocalDateTime.of(2024, 2, 3, 4, 5, 6);
        User domainUser = new User(
                null,
                "new-user@example.com",
                "newbie",
                "password",
                AuthProvider.GITHUB,
                UserRole.ADMIN,
                UserStatus.ACTIVE,
                createdAt
        );

        UserEntity entity = UserEntity.fromDomain(domainUser);
        User converted = entity.toDomain();

        assertNull(converted.id());
    }

    /**
     * 엔티티를 다시 도메인 모델로 변환했을 때 값이 동일하게 유지되는지 검증한다.
     */
    @Test
    void toDomainShouldReturnUserWithSameValues() {
        LocalDateTime createdAt = LocalDateTime.of(2023, 12, 31, 23, 59, 59);
        User original = new User(
                UUID.fromString("123e4567-e89b-12d3-a456-426614174111"),
                "legacy@example.com",
                "legacy-nick",
                "legacy-pass",
                AuthProvider.LOCAL,
                UserRole.MANAGER,
                UserStatus.INACTIVE,
                createdAt
        );

        UserEntity entity = UserEntity.fromDomain(original);
        User converted = entity.toDomain();

        assertEquals(original, converted);
    }
}
