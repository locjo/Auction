package com.auction.system.modules.bid.controller;

import com.auction.system.common.ApiResponse;
import com.auction.system.modules.bid.dto.request.PlaceBidRequest;
import com.auction.system.modules.bid.dto.response.BidResponse;
import com.auction.system.modules.bid.service.BidService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/products/{productId}/bids")
public class BidController {
    private final BidService bidService;

    public BidController(BidService bidService) {
        this.bidService = bidService;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ApiResponse<BidResponse> placeBid(@PathVariable Long productId,
                                             @Valid @RequestBody PlaceBidRequest request,
                                             Authentication authentication) {
        return ApiResponse.success("Đặt giá thành công",
                bidService.placeBid(productId, authentication.getName(), request));
    }
}
