package com.auction.system.modules.bid.dto.response;

import com.auction.system.modules.bid.entity.Bid;

import java.math.BigDecimal;
import java.time.Instant;

public record BidResponse(Long id, Long productId, Long userId, String bidderName,
                          BigDecimal bidAmount, Instant createdAt) {
    public static BidResponse from(Bid bid) {
        return new BidResponse(bid.getId(), bid.getProduct().getId(), bid.getUser().getId(),
                bid.getUser().getFullName(), bid.getBidAmount(), bid.getCreatedAt());
    }
}
