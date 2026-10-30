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
    @Enumerated(EnumType.STRING) @Column(name = "kyc_status", nullable = false, length = 20,
            columnDefinition = "varchar(20) not null default 'NOT_SUBMITTED'")
    private KycStatus kycStatus = KycStatus.NOT_SUBMITTED;
    @Column(name = "identity_number", length = 32)
    private String identityNumber;
    @Column(name = "identity_document_url", length = 2048)
    private String identityDocumentUrl;
    @Column(name = "phone_verified", nullable = false, columnDefinition = "boolean not null default false")
    private boolean phoneVerified;
    @Column(name = "email_verified", nullable = false, columnDefinition = "boolean not null default false")
    private boolean emailVerified;
    @Column(name = "kyc_rejection_reason", length = 500)
    private String kycRejectionReason;
    @Version
    @Column(nullable = false, columnDefinition = "bigint not null default 0")
    private long version;
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

    public void reviewKyc(boolean approved, String reason) {
        if (kycStatus != KycStatus.PENDING) throw new IllegalStateException("KYC request is not pending");
        if (approved) {
            if (identityNumber == null || identityDocumentUrl == null || phone == null || phone.isBlank())
                throw new IllegalStateException("KYC data or phone number is incomplete");
            kycStatus = KycStatus.APPROVED;
            phoneVerified = true;
            emailVerified = true;
            kycRejectionReason = null;
        } else {
            kycStatus = KycStatus.REJECTED;
            kycRejectionReason = reason;
        }
    }

    public void approveSeller() {
        if (kycStatus != KycStatus.APPROVED) throw new IllegalStateException("KYC must be approved first");
        if (status != UserStatus.ACTIVE) throw new IllegalStateException("Only active users can become sellers");
        role = UserRole.SELLER;
    }

    public void lock() { status = UserStatus.LOCKED; }
    public void unlock() { status = UserStatus.ACTIVE; }

    public void submitKyc(String identityNumber, String identityDocumentUrl) {
        if (kycStatus == KycStatus.PENDING || kycStatus == KycStatus.APPROVED)
            throw new IllegalStateException("KYC request cannot be submitted in current state");
        this.identityNumber = identityNumber;
        this.identityDocumentUrl = identityDocumentUrl;
        this.kycStatus = KycStatus.PENDING;
        this.kycRejectionReason = null;
    }
}
