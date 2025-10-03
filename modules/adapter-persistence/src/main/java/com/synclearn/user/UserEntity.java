package com.synclearn.user;

import com.synclearn.user.enums.AuthProvider;
import com.synclearn.user.enums.UserRole;
import com.synclearn.user.enums.UserStatus;
import jakarta.persistence.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;
import java.util.UUID;

/**
 * 사용자 정보를 영속화하는 JPA 엔티티.
 * 도메인 모델 {@link User}와의 변환 헬퍼를 제공한다.
 */
@Entity
@Table(name = "users")
@EntityListeners(AuditingEntityListener.class)
public class UserEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(columnDefinition = "UUID")
    private UUID id;

    @Column(nullable = false, unique = true, length = 100)
    private String email;

    @Column(nullable = false)
    private String password;

    @Column(length = 20)
    private String nickname;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private AuthProvider provider;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private UserRole role;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private UserStatus status;

    @CreatedDate
    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    protected UserEntity() { } // JPA 기본 생성자

    private UserEntity(UUID id, String email, String password, String nickname,
                       AuthProvider provider, UserRole role,
                       UserStatus status, LocalDateTime createdAt) {
        this.id = id;
        this.email = email;
        this.password = password;
        this.nickname = nickname;
        this.provider = provider;
        this.role = role;
        this.status = status;
        this.createdAt = createdAt;
    }

    /**
     * 도메인 모델을 영속 엔티티로 변환한다.
     *
     * @param user 변환 대상 사용자 도메인 모델
     * @return 전달받은 값을 그대로 가진 엔티티 인스턴스
     */
    public static UserEntity fromDomain(User user) {
        return new UserEntity(
                user.id(),
                user.email(),
                user.password(),
                user.nickname(),
                user.provider(),
                user.role(),
                user.status(),
                user.createdAt()
        );
    }

    /**
     * 엔티티를 도메인 모델로 변환한다.
     *
     * @return 엔티티가 보유한 값을 복사한 도메인 모델
     */
    public User toDomain() {
        return new User(
                id,
                email,
                nickname,
                password,
                provider,
                role,
                status,
                createdAt
        );
    }
}
