package com.auction.system.modules.wallet.entity;

import com.auction.system.modules.user.entity.User;
import com.auction.system.common.AuditableEntity;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "wallets", uniqueConstraints = @UniqueConstraint(name = "uk_wallets_user", columnNames = "user_id"))
public class Wallet extends AuditableEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "user_id", nullable = false, unique = true)
    private User user;

    @Column(nullable = false, precision = 19, scale = 2)
    private BigDecimal balance = BigDecimal.ZERO;

    @Column(name = "locked_balance", nullable = false, precision = 19, scale = 2)
    private BigDecimal lockedBalance = BigDecimal.ZERO;

    public Wallet(User user) {
        this.user = user;
    }
}
