package com.auction.system.modules.wallet.controller;

import com.auction.system.common.ApiResponse;
import com.auction.system.modules.wallet.dto.response.WalletResponse;
import com.auction.system.modules.wallet.service.WalletService;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/wallets")
public class WalletController {
    private final WalletService walletService;

    public WalletController(WalletService walletService) {
        this.walletService = walletService;
    }

    @GetMapping("/me")
    public ApiResponse<WalletResponse> getCurrentWallet(Authentication authentication) {
        return ApiResponse.success("Wallet retrieved successfully", walletService.getWallet(authentication.getName()));
    }
}
