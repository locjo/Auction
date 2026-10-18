package com.auction.system.modules.user.dto.response;

import com.auction.system.modules.user.entity.User;

import java.time.Instant;

public record UserProfileResponse(
        Long id,
        String email,
        String fullName,
        String phone,
        String role,
        String status,
        Instant createdAt
) {
    public static UserProfileResponse from(User user) {
        return new UserProfileResponse(
                user.getId(),
                user.getEmail(),
                user.getFullName(),
                user.getPhone(),
                user.getRole().name(),
                user.getStatus().name(),
                user.getCreatedAt()
        );
    }
}
