package com.auction.system.modules.user.entity;

import jakarta.persistence.*;
import com.auction.system.common.AuditableEntity;
import com.auction.system.common.AuthProvider;
import com.auction.system.common.UserRole;
import com.auction.system.common.UserStatus;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "users", uniqueConstraints = @UniqueConstraint(name = "uk_users_email", columnNames = "email"))
public class User extends AuditableEntity {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(nullable = false, unique = true, length = 255)
    private String email;
    @Column(name = "password", nullable = false)
    private String passwordHash;
    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20, columnDefinition = "varchar(20) default 'LOCAL'")
    private AuthProvider provider = AuthProvider.LOCAL;
    @Column(name = "provider_id", length = 255)
    private String providerId;
    @Column(name = "avatar_url", length = 2048)
    private String avatarUrl;
    @Column(name = "full_name", nullable = false, length = 100)
    private String fullName;
    @Column(length = 20)
    private String phone;
    @Enumerated(EnumType.STRING) @Column(nullable = false, length = 50)
    private UserRole role = UserRole.BIDDER;
    @Enumerated(EnumType.STRING) @Column(nullable = false, length = 50)
    private UserStatus status = UserStatus.ACTIVE;
    public User(String email, String passwordHash, String fullName, String phone) {
        this.email = email; this.passwordHash = passwordHash; this.fullName = fullName; this.phone = phone;
    }
    public User(String email, String passwordHash, String fullName, String providerId, String avatarUrl, AuthProvider provider) {
        this.email = email;
        this.passwordHash = passwordHash;
        this.fullName = fullName;
        this.provider = provider;
        this.providerId = providerId;
        this.avatarUrl = avatarUrl;
        this.role = UserRole.BIDDER;
        this.status = UserStatus.ACTIVE;
    }
    public void updateGoogleProfile(String fullName, String providerId, String avatarUrl) {
        this.fullName = fullName;
        this.provider = AuthProvider.GOOGLE;
        this.providerId = providerId;
        this.avatarUrl = avatarUrl;
    }
}
