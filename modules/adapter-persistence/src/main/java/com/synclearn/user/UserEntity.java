package com.synclearn.user;

import com.synclearn.user.enums.AuthProvider;
import com.synclearn.user.enums.UserRole;
import com.synclearn.user.enums.UserStatus;
import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "users")
public class UserEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

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

    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    protected UserEntity() { } // JPA 기본 생성자

    private UserEntity(Long id, String email, String password, String nickname,
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

    // 도메인 -> 엔티티 변환
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

    // 엔티티 -> 도메인 변환
    public User toDomain() {
        return new User(
                id,
                email,
                password,
                nickname,
                provider,
                role,
                status,
                createdAt
        );
    }
}
