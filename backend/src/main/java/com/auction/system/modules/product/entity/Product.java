package com.auction.system.modules.product.entity;

import java.math.BigDecimal;
import java.time.Instant;

import com.auction.system.common.AuditableEntity;
import com.auction.system.modules.category.entity.Category;
import com.auction.system.modules.user.entity.User;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Index;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.persistence.Version;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "products", indexes = {
        @Index(name = "idx_products_seller", columnList = "seller_id"),
        @Index(name = "idx_products_category", columnList = "category_id")
})
public class Product extends AuditableEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "seller_id", nullable = false)
    private User seller;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "category_id", nullable = false)
    private Category category;

    @Column(nullable = false, length = 200)
    private String title;

    @Column(columnDefinition = "text")
    private String description;

    @Column(name = "initial_price", nullable = false, precision = 19, scale = 2)
    private BigDecimal initialPrice;

    @Column(name = "current_price", nullable = false, precision = 19, scale = 2)
    private BigDecimal currentPrice;

    @Column(name = "start_time", nullable = false)
    private Instant startTime;

    @Column(name = "end_time", nullable = false)
    private Instant endTime;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 30)
    private Status status = Status.PENDING;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "winner_id")
    private User winner;

    @Version
    @Column(nullable = false)
    private Long version;

    public Product(User seller, Category category, String title, String description,
                   BigDecimal initialPrice, Instant startTime, Instant endTime) {
        this.seller = seller;
        this.category = category;
        this.title = title;
        this.description = description;
        this.initialPrice = initialPrice;
        this.currentPrice = initialPrice;
        this.startTime = startTime;
        this.endTime = endTime;
        this.status = Status.PENDING;
    }

    public void approve(Instant now) {
        this.status = startTime.isAfter(now) ? Status.SCHEDULED : Status.ACTIVE;
    }

    public void activate() {
        this.status = Status.ACTIVE;
    }

    public void finish() {
        this.status = Status.ENDED;
    }

    public void cancel() {
        this.status = Status.CANCELLED;
    }

    public void updateCurrentPrice(BigDecimal currentPrice) {
        this.currentPrice = currentPrice;
    }

    public enum Status { PENDING, ACTIVE, SCHEDULED, ENDED, CANCELLED }
}
