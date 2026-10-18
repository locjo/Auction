package com.auction.system.modules.product.dto.request;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.FutureOrPresent;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.math.BigDecimal;
import java.time.Instant;

public record CreateProductRequest(
        @NotNull Long categoryId,
        @NotBlank @Size(max = 200) String title,
        @Size(max = 10000) String description,
        @NotNull @DecimalMin(value = "0.01") BigDecimal initialPrice,
        @NotNull @FutureOrPresent Instant startTime,
        @NotNull @Future Instant endTime
) {}
