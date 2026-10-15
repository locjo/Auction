package com.auction.system.modules.auth.dto.response;

import java.time.Instant;

public record AuthResponse(String tokenType, String accessToken, Instant accessTokenExpiresAt,
                           UserInfo user) {
    public record UserInfo(Long id, String email, String fullName, String role) {}
}
