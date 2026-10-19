package com.auction.system.modules.auth.entity;
import java.time.Instant;
import java.util.UUID;

import com.auction.system.common.AuditableEntity;
import com.auction.system.modules.user.entity.User;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
import jakarta.persistence.Index;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import lombok.AccessLevel;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "auth_refresh_tokens",
        indexes = @Index(name = "idx_refresh_token_user", columnList = "user_id"),
        uniqueConstraints = @UniqueConstraint(name = "uk_refresh_token_jti", columnNames = "token_id"))
public class RefreshToken extends AuditableEntity {
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

    public RefreshToken(String tokenId, User user, Instant expiresAt) {
        this.tokenId = tokenId;
        this.user = user;
        this.expiresAt = expiresAt;
    }

    public void revoke() { this.revoked = true; }
}
