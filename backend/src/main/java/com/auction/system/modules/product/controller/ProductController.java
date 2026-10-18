package com.auction.system.modules.product.controller;

import com.auction.system.common.ApiResponse;
import com.auction.system.exception.AppException;
import com.auction.system.exception.ErrorCode;
import com.auction.system.modules.product.dto.request.CreateProductRequest;
import com.auction.system.modules.product.dto.response.ProductResponse;
import com.auction.system.modules.product.service.ProductService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/v1/products")
public class ProductController {
    private final ProductService productService;

    public ProductController(ProductService productService) {
        this.productService = productService;
    }

    @GetMapping
    public ApiResponse<List<ProductResponse>> getPublicProducts(
            @RequestParam(required = false) String status,
            @RequestParam(required = false) Long categoryId,
            @RequestParam(required = false) String keyword) {
        if (status != null && !"ACTIVE".equalsIgnoreCase(status)) {
            throw new AppException(ErrorCode.INVALID_INPUT, "Public products only support status=ACTIVE");
        }
        return ApiResponse.success("Products retrieved successfully",
                productService.getPublicProducts(categoryId, keyword));
    }

    @GetMapping("/{id}")
    public ApiResponse<ProductResponse> getPublicProduct(@PathVariable Long id) {
        return ApiResponse.success("Product retrieved successfully", productService.getPublicProduct(id));
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ApiResponse<ProductResponse> create(@Valid @RequestBody CreateProductRequest request,
                                               Authentication authentication) {
        return ApiResponse.success("Product submitted for review",
                productService.create(authentication.getName(), request));
    }

    @GetMapping("/seller/me")
    public ApiResponse<List<ProductResponse>> getSellerProducts(Authentication authentication) {
        return ApiResponse.success("Seller products retrieved successfully",
                productService.getSellerProducts(authentication.getName()));
    }

    @GetMapping("/admin/pending")
    public ApiResponse<List<ProductResponse>> getPendingProducts() {
        return ApiResponse.success("Pending products retrieved successfully", productService.getPendingProducts());
    }

    @PatchMapping("/admin/{id}/approve")
    public ApiResponse<ProductResponse> approve(@PathVariable Long id) {
        return ApiResponse.success("Product approved successfully", productService.approve(id));
    }

    @PatchMapping("/admin/{id}/cancel")
    public ApiResponse<ProductResponse> cancel(@PathVariable Long id) {
        return ApiResponse.success("Product cancelled successfully", productService.cancel(id));
    }
}
