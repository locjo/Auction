package com.auction.system.modules.user.dto.response;

import com.auction.system.common.UserRole;
import com.auction.system.common.UserStatus;
import com.auction.system.modules.user.entity.KycStatus;
import com.auction.system.modules.user.entity.User;
import java.time.Instant;

public record AdminUserResponse(Long id, String email, String fullName, String phone,
                                UserRole role, UserStatus status, KycStatus kycStatus,
                                String identityNumber, String identityDocumentUrl,
                                boolean phoneVerified, boolean emailVerified,
                                String kycRejectionReason, Instant createdAt) {
    public static AdminUserResponse from(User user) {
        return new AdminUserResponse(user.getId(), user.getEmail(), user.getFullName(), user.getPhone(),
                user.getRole(), user.getStatus(), user.getKycStatus(), user.getIdentityNumber(),
                user.getIdentityDocumentUrl(), user.isPhoneVerified(), user.isEmailVerified(),
                user.getKycRejectionReason(), user.getCreatedAt());
    }
}
