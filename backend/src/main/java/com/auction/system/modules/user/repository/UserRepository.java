package com.auction.system.modules.user.repository;

import com.auction.system.common.AuthProvider;

import com.auction.system.modules.user.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import com.auction.system.common.UserRole;
import com.auction.system.common.UserStatus;
import com.auction.system.modules.user.entity.KycStatus;
import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long>, JpaSpecificationExecutor<User> {
    Optional<User> findByEmailIgnoreCase(String email);
    Optional<User> findByProviderAndProviderId(AuthProvider provider, String providerId);
    boolean existsByEmailIgnoreCase(String email);
    Page<User> findByEmailContainingIgnoreCaseOrFullNameContainingIgnoreCase(String email, String fullName, Pageable pageable);
    Page<User> findByStatus(UserStatus status, Pageable pageable);
    Page<User> findByRole(UserRole role, Pageable pageable);
    Page<User> findByKycStatus(KycStatus status, Pageable pageable);

}
