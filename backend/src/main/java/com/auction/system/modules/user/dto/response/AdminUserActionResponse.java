package com.auction.system.modules.user.dto.response;

import java.time.Instant;

public record AdminUserActionResponse(
        Long userId,
        String action,
        String reason,
        String performedBy,
        Instant performedAt
) { }
