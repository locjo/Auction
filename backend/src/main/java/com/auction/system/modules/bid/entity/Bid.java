package com.auction.system.modules.bid.entity;

import com.auction.system.modules.product.entity.Product;
import com.auction.system.modules.user.entity.User;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.Instant;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "bids", indexes = {
        @Index(name = "idx_bids_product", columnList = "product_id"),
        @Index(name = "idx_bids_user", columnList = "user_id")
})
public class Bid {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "product_id", nullable = false)
    private Product product;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(name = "bid_amount", nullable = false, precision = 19, scale = 2)
    private BigDecimal bidAmount;

    @Column(name = "created_at", nullable = false, updatable = false)
    private Instant createdAt = Instant.now();

    public Bid(Product product, User user, BigDecimal bidAmount) {
        this.product = product;
        this.user = user;
        this.bidAmount = bidAmount;
    }
}
