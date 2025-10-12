package com.synclearn.user;

import com.synclearn.user.enums.AuthProvider;

import java.nio.channels.FileChannel;
import java.util.Optional;

public interface UserRepository {
    Optional<User> findByEmail(String email);
    User updateProvider(User user, AuthProvider authProvider);
    User save(User user);
    boolean existsByEmail(String email);
    boolean existsByProviderId(String providerId);
}
