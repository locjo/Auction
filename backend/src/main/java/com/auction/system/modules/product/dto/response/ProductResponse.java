package com.auction.system.modules.product.dto.response;

import com.auction.system.modules.product.entity.Product;

import java.math.BigDecimal;
import java.time.Instant;

public record ProductResponse(
        Long id,
        String title,
        String description,
        Long categoryId,
        String categoryName,
        Long sellerId,
        String sellerName,
        BigDecimal initialPrice,
        BigDecimal currentPrice,
        Instant startTime,
        Instant endTime,
        Product.Status status,
        long bidCount,
        Instant createdAt
) {
    public static ProductResponse from(Product product) {
        return from(product, 0);
    }

    public static ProductResponse from(Product product, long bidCount) {
        return new ProductResponse(product.getId(), product.getTitle(), product.getDescription(),
                product.getCategory().getId(), product.getCategory().getName(),
                product.getSeller().getId(), product.getSeller().getFullName(),
                product.getInitialPrice(), product.getCurrentPrice(), product.getStartTime(),
                product.getEndTime(), product.getStatus(), bidCount, product.getCreatedAt());
    }
}
