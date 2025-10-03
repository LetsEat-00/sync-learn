package com.synclearn.user;

import org.flywaydb.core.Flyway;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.ImportAutoConfiguration;
import org.springframework.boot.autoconfigure.flyway.FlywayAutoConfiguration;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.springframework.jdbc.core.JdbcTemplate;

import com.synclearn.user.enums.AuthProvider;
import com.synclearn.user.enums.UserRole;
import com.synclearn.user.enums.UserStatus;

import java.util.Optional;

@DataJpaTest
@Testcontainers
@ActiveProfiles("test")
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@ContextConfiguration(classes = com.synclearn.TestApp.class) // java-library라 별도 실행 Class 연결
@ImportAutoConfiguration(FlywayAutoConfiguration.class)
class UserIntegrationTest {

    // TestContainer 에서 사용할 컨테이너 및 파라미터 설정
    @Container
    static PostgreSQLContainer<?> postgres =  new PostgreSQLContainer<>("postgres:16-alpine3.22")
                    .withDatabaseName("synclearn")
                    .withUsername("postgres")
                    .withPassword("postgres");

    /**
     * 동적으로 실행 properties 설정
     */
    @DynamicPropertySource
    static void registerProps(DynamicPropertyRegistry registry) {
        // DB 연결정보 설정
        registry.add("spring.datasource.url", postgres::getJdbcUrl);
        registry.add("spring.datasource.username", postgres::getUsername);
        registry.add("spring.datasource.password", postgres::getPassword);

        // 스키마 관련 설정
        registry.add("spring.jpa.hibernate.ddl-auto", () -> "none");
        registry.add("spring.flyway.enabled", () -> true);
        registry.add("spring.flyway.locations", () -> "classpath:db/migration");

        // 아래 설정은 test 에서만 활성화, 운영에선 clean은 비활성화 처리해야함.
        registry.add("spring.flyway.clean-disabled", () -> false);
    }

    @Autowired
    Flyway flyway;

    @Autowired
    JdbcTemplate jdbcTemplate;

    @Autowired
    UserJpaRepository userJpaRepository;

    /**
     * 각 테스트 시작 시 데이터베이스를 비우는 역할
     * 테스트들이 항상 초기화된 DB에서 테스트를 실행할 수 있는 역할을 함
     */
    @BeforeEach
    void migrate() {
        flyway.clean();
        flyway.migrate();
    }

    /**
     * DB 컨테이너가 정상적으로 실행되었는지 검증
     * (동시에 DB가 잘 연결되었는지 확인)
     */
    @Test
    void sanity() {
        Assertions.assertTrue(postgres.isRunning());
    }

    /**
     * Flyway로 User 관련 테이블들이 정상적으로 생성되었는지 확인
     */
    @Test
    void usersTableShouldExist() {
        Boolean exists = jdbcTemplate.queryForObject(
                """
                    SELECT
                        EXISTS (
                            SELECT
                                1
                            FROM
                                information_schema.tables
                            WHERE
                                table_schema='public'
                                AND table_name='users'
                        )
                    """,
                Boolean.class
        );
        Assertions.assertEquals(Boolean.TRUE, exists, "users 테이블이 존재해야 합니다.");
    }

    /**
     * User 엔티티 저장/조회 통합 검증.
     * - given: 도메인 User를 준비하여 엔티티로 변환한다.
     * - when: JPA Repository.save로 저장한다.
     * - then:
     *   - 이메일로 조회 시 존재해야 한다.
     *   - 저장 시 UUID가 생성되어야 한다.
     *   - @CreatedDate(Auditing)로 createdAt이 저장 직후 및 재조회 시 null이 아니어야 한다.
     *   - email/nickname/provider 값이 입력과 일치해야 한다.
     */
    @Test
    void shouldPersistAndFindUser() {
        // given
        User domainUser = new User(
                null,
                "alice@example.com",
                "alice",
                "secret123",
                AuthProvider.LOCAL,
                UserRole.USER,
                UserStatus.ACTIVE,
                null
        );

        // when
        UserEntity saved = userJpaRepository.save(UserEntity.fromDomain(domainUser));

        // then
        Optional<UserEntity> found = userJpaRepository.findByEmail("alice@example.com");
        Assertions.assertTrue(found.isPresent(), "저장한 사용자를 이메일로 조회할 수 있어야 합니다.");
        Assertions.assertNotNull(saved, "저장 결과 엔티티가 null이 아니어야 합니다.");
        Assertions.assertNotNull(saved.toDomain().createdAt(), "Auditing으로 저장 직후 createdAt이 설정되어야 합니다.");
        Assertions.assertNotNull(found.get().toDomain().id(), "저장 시 UUID가 생성되어야 합니다.");
        Assertions.assertNotNull(found.get().toDomain().createdAt(), "저장 시 CreatedAt이 생성되어야 합니다.");
        Assertions.assertEquals("alice@example.com", found.get().toDomain().email());
        Assertions.assertEquals("alice", found.get().toDomain().nickname());
        Assertions.assertEquals(AuthProvider.LOCAL, found.get().toDomain().provider());
    }
}
