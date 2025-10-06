package com.synclearn.user;

import com.synclearn.user.enums.AuthProvider;
import org.springframework.stereotype.Repository;
import java.util.Optional;

@Repository
public class UserPersistenceAdapter implements UserRepository {

    private final UserJpaRepository jpaRepository;

    public UserPersistenceAdapter(UserJpaRepository jpaRepository) {
        this.jpaRepository = jpaRepository;
    }


    @Override
    public Optional<User> findByEmail(String email) {
        return jpaRepository.findByEmail(email)
                .map(UserEntity::toDomain);
    }

    @Override
    public User updateProvider(User user, AuthProvider authProvider) {
        UserEntity userEntity = UserEntity.fromDomain(user);
        userEntity.setProvider(authProvider);
        jpaRepository.save(userEntity);

        return userEntity.toDomain();
    }

    @Override
    public User save(User user) {
        jpaRepository.save(UserEntity.fromDomain(user));
        return user;
    }
}