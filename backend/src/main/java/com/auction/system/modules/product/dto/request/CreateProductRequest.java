package com.auction.system.modules.product.dto.request;

import java.math.BigDecimal;
import java.time.Instant;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.FutureOrPresent;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record CreateProductRequest(
        @NotNull(message = "Mã danh mục không được để trống") Long categoryId,
        @NotBlank(message = "Tên sản phẩm không được để trống")
        @Size(max = 200, message = "Tên sản phẩm không được vượt quá 200 ký tự") String title,
        @Size(max = 10000, message = "Mô tả không được vượt quá 10000 ký tự") String description,
        @NotNull(message = "Giá khởi điểm không được để trống")
        @DecimalMin(value = "0.01", message = "Giá khởi điểm phải lớn hơn 0") BigDecimal initialPrice,
        @NotNull(message = "Thời gian bắt đầu không được để trống")
        @FutureOrPresent(message = "Thời gian bắt đầu phải ở hiện tại hoặc trong tương lai") Instant startTime,
        @NotNull(message = "Thời gian kết thúc không được để trống")
        @Future(message = "Thời gian kết thúc phải ở tương lai") Instant endTime
) {}
