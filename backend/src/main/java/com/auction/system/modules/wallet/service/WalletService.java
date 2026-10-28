package com.auction.system.modules.wallet.service;

import com.auction.system.exception.ErrorCode;

import com.auction.system.exception.AppException;

import com.auction.system.modules.wallet.dto.response.WalletResponse;
import com.auction.system.modules.wallet.repository.WalletRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class WalletService {
    private final WalletRepository wallets;

    public WalletService(WalletRepository wallets) {
        this.wallets = wallets;
    }

    @Transactional(readOnly = true)
    public WalletResponse getWallet(String email) {
        var wallet = wallets.findByUserEmailIgnoreCase(email)
                .orElseThrow(() -> new AppException(ErrorCode.RESOURCE_NOT_FOUND));
        return WalletResponse.from(wallet);
    }
}
