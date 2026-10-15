package com.auction.system.modules.auth.entity;

import com.auction.system.modules.user.entity.User;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.Instant;
import java.util.UUID;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "auth_refresh_tokens",
        indexes = @Index(name = "idx_refresh_token_user", columnList = "user_id"),
        uniqueConstraints = @UniqueConstraint(name = "uk_refresh_token_jti", columnNames = "token_id"))
public class RefreshToken {
    @Id
    @Column(nullable = false, updatable = false, length = 36)
    private String id = UUID.randomUUID().toString();

    @Column(name = "token_id", nullable = false, unique = true, length = 36)
    private String tokenId;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(name = "expires_at", nullable = false)
    private Instant expiresAt;

    @Column(nullable = false)
    private boolean revoked = false;

    @Column(name = "created_at", nullable = false, updatable = false)
    private Instant createdAt = Instant.now();

    public RefreshToken(String tokenId, User user, Instant expiresAt) {
        this.tokenId = tokenId;
        this.user = user;
        this.expiresAt = expiresAt;
    }

    public void revoke() { this.revoked = true; }
}
