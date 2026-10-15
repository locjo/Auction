package com.auction.system.modules.auth.repository;

import com.auction.system.modules.auth.entity.RefreshToken;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface RefreshTokenRepository extends JpaRepository<RefreshToken, String> {
    Optional<RefreshToken> findByTokenIdAndRevokedFalse(String tokenId);
    long deleteByTokenId(String tokenId);
}
