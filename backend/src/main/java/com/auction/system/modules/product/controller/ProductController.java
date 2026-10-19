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
            @RequestParam(required = false) String keyword) {
        if (status != null && !"ACTIVE".equalsIgnoreCase(status)) {
            throw new AppException(ErrorCode.INVALID_INPUT, "Chỉ hỗ trợ hiển thị sản phẩm có trạng thái ACTIVE");
        }
        return ApiResponse.success("Lấy danh sách sản phẩm thành công",
                productService.getPublicProducts(keyword));
    }

    @GetMapping("/{id}")
    public ApiResponse<ProductResponse> getPublicProduct(@PathVariable Long id) {
        return ApiResponse.success("Lấy thông tin sản phẩm thành công", productService.getPublicProduct(id));
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ApiResponse<ProductResponse> create(@Valid @RequestBody CreateProductRequest request,
                                               Authentication authentication) {
        return ApiResponse.success("Gửi sản phẩm chờ duyệt thành công",
                productService.create(authentication.getName(), request));
    }

    @GetMapping("/seller/me")
    public ApiResponse<List<ProductResponse>> getSellerProducts(Authentication authentication) {
        return ApiResponse.success("Lấy danh sách sản phẩm của người bán thành công",
                productService.getSellerProducts(authentication.getName()));
    }

    @GetMapping("/admin/pending")
    public ApiResponse<List<ProductResponse>> getPendingProducts() {
        return ApiResponse.success("Lấy danh sách sản phẩm chờ duyệt thành công", productService.getPendingProducts());
    }

    @PatchMapping("/admin/{id}/approve")
    public ApiResponse<ProductResponse> approve(@PathVariable Long id) {
        return ApiResponse.success("Duyệt sản phẩm thành công", productService.approve(id));
    }

    @PatchMapping("/admin/{id}/cancel")
    public ApiResponse<ProductResponse> cancel(@PathVariable Long id) {
        return ApiResponse.success("Hủy sản phẩm thành công", productService.cancel(id));
    }
}
