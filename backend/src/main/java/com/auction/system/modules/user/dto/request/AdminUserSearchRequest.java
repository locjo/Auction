package com.auction.system.modules.user.dto.request;

import com.auction.system.common.UserRole;
import com.auction.system.common.UserStatus;
import com.auction.system.modules.user.entity.KycStatus;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Size;

public record AdminUserSearchRequest(
        @Size(max = 255) String q,
        UserStatus status,
        UserRole role,
        KycStatus kycStatus,
        @Min(0) Integer page,
        @Min(1) @Max(100) Integer size
) {
    public int pageNumber() { return page == null ? 0 : page; }
    public int pageSize() { return size == null ? 20 : size; }
}
