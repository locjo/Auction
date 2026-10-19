package com.auction.system.modules.bid.dto.request;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;

public record PlaceBidRequest(
        @NotNull @DecimalMin(value = "0.01", message = "Giá đặt phải lớn hơn 0")
        BigDecimal bidAmount
) {}
