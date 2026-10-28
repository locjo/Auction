package com.auction.system.modules.user.repository;

import com.auction.system.modules.user.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByEmailIgnoreCase(String email);
    Optional<User> findByProviderAndProviderId(User.AuthProvider provider, String providerId);
    boolean existsByEmailIgnoreCase(String email);
}
