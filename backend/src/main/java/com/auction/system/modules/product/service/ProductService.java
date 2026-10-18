package com.auction.system.modules.product.service;

import com.auction.system.exception.AppException;
import com.auction.system.exception.ErrorCode;
import com.auction.system.modules.category.entity.Category;
import com.auction.system.modules.category.repository.CategoryRepository;
import com.auction.system.modules.product.dto.request.CreateProductRequest;
import com.auction.system.modules.product.dto.response.ProductResponse;
import com.auction.system.modules.product.entity.Product;
import com.auction.system.modules.product.repository.ProductRepository;
import com.auction.system.modules.user.entity.User;
import com.auction.system.modules.user.repository.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class ProductService {
    private final ProductRepository products;
    private final CategoryRepository categories;
    private final UserRepository users;

    public ProductService(ProductRepository products, CategoryRepository categories, UserRepository users) {
        this.products = products;
        this.categories = categories;
        this.users = users;
    }

    @Transactional(readOnly = true)
    public List<ProductResponse> getPublicProducts(Long categoryId, String keyword) {
        return products.search(Product.Status.ACTIVE, categoryId, normalizeKeyword(keyword))
                .stream().map(ProductResponse::from).toList();
    }

    @Transactional(readOnly = true)
    public ProductResponse getPublicProduct(Long id) {
        Product product = products.findById(id)
                .filter(item -> item.getStatus() == Product.Status.ACTIVE)
                .orElseThrow(() -> new AppException(ErrorCode.RESOURCE_NOT_FOUND, "Product not found"));
        return ProductResponse.from(product);
    }

    @Transactional
    public ProductResponse create(String sellerEmail, CreateProductRequest request) {
        if (!request.endTime().isAfter(request.startTime())) {
            throw new AppException(ErrorCode.INVALID_INPUT, "endTime must be after startTime");
        }
        User seller = users.findByEmailIgnoreCase(sellerEmail)
                .orElseThrow(() -> new AppException(ErrorCode.RESOURCE_NOT_FOUND, "Seller not found"));
        Category category = categories.findById(request.categoryId())
                .orElseThrow(() -> new AppException(ErrorCode.RESOURCE_NOT_FOUND, "Category not found"));
        Product product = products.save(new Product(seller, category, request.title().trim(),
                request.description(), request.initialPrice(), request.startTime(), request.endTime()));
        return ProductResponse.from(product);
    }

    @Transactional(readOnly = true)
    public List<ProductResponse> getSellerProducts(String sellerEmail) {
        return products.findBySellerEmailIgnoreCaseOrderByCreatedAtDesc(sellerEmail)
                .stream().map(ProductResponse::from).toList();
    }

    @Transactional(readOnly = true)
    public List<ProductResponse> getPendingProducts() {
        return products.findByStatusOrderByCreatedAtAsc(Product.Status.PENDING)
                .stream().map(ProductResponse::from).toList();
    }

    @Transactional
    public ProductResponse approve(Long id) {
        Product product = findProduct(id);
        if (product.getStatus() != Product.Status.PENDING) {
            throw new AppException(ErrorCode.CONFLICT, "Only pending products can be approved");
        }
        product.approve();
        return ProductResponse.from(product);
    }

    @Transactional
    public ProductResponse cancel(Long id) {
        Product product = findProduct(id);
        if (product.getStatus() != Product.Status.PENDING && product.getStatus() != Product.Status.ACTIVE) {
            throw new AppException(ErrorCode.CONFLICT, "Only pending or active products can be cancelled");
        }
        product.cancel();
        return ProductResponse.from(product);
    }

    private Product findProduct(Long id) {
        return products.findById(id)
                .orElseThrow(() -> new AppException(ErrorCode.RESOURCE_NOT_FOUND, "Product not found"));
    }

    private String normalizeKeyword(String keyword) {
        if (keyword == null || keyword.isBlank()) return null;
        return keyword.trim();
    }
}
