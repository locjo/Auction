package com.auction.system.modules.wallet.dto.response;

import com.auction.system.modules.wallet.entity.Wallet;

import java.math.BigDecimal;

public record WalletResponse(BigDecimal balance, BigDecimal lockedBalance) {
    public static WalletResponse from(Wallet wallet) {
        return new WalletResponse(wallet.getBalance(), wallet.getLockedBalance());
    }
}
