package com.auction.system.modules.payment.entity;

import com.auction.system.modules.user.entity.User;
import com.auction.system.modules.order.entity.Order;
import com.auction.system.common.AuditableEntity;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "transactions", indexes = {
        @Index(name = "idx_transactions_user", columnList = "user_id"),
        @Index(name = "idx_transactions_order", columnList = "order_id")
})
public class Transaction extends AuditableEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "order_id")
    private Order order;

    @Column(nullable = false, precision = 19, scale = 2)
    private BigDecimal amount;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 30)
    private Type type;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 30)
    private Status status;

    /** VNPay transaction number returned after payment processing. */
    @Column(name = "vnp_transaction_no", length = 255)
    private String vnpTransactionNo;

    public enum Type { DEPOSIT, PAYMENT, BID_DEPOSIT }
    public enum Status { PENDING, SUCCESS, FAILED }
}
