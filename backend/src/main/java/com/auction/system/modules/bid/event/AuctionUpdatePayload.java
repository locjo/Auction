package com.auction.system.modules.bid.event;

import java.math.BigDecimal;
import java.time.Instant;

public record AuctionUpdatePayload(
        Long productId,
        BigDecimal currentPrice,
        String bidderName,
        BigDecimal bidAmount,
        Instant endTime,
        Instant createdAt) {
}
