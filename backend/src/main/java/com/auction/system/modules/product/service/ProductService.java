package com.auction.system.modules.product.service;

import com.auction.system.exception.AppException;
import com.auction.system.exception.ErrorCode;
import com.auction.system.modules.bid.repository.BidRepository;
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
import java.util.ArrayList;
import java.util.Comparator;
import java.time.Instant;
import org.springframework.scheduling.annotation.Scheduled;

@Service
public class ProductService {
    private final ProductRepository products;
    private final CategoryRepository categories;
    private final UserRepository users;
    private final BidRepository bids;

    public ProductService(ProductRepository products, CategoryRepository categories, UserRepository users,
                          BidRepository bids) {
        this.products = products;
        this.categories = categories;
        this.users = users;
        this.bids = bids;
    }

    @Transactional(readOnly = true)
    public List<ProductResponse> getPublicProducts(String keyword) {
        return getPublicProducts(keyword, false);
    }

    @Transactional(readOnly = true)
    public List<ProductResponse> getPublicProducts(String keyword, boolean includeEnded) {
        String normalizedKeyword = normalizeKeyword(keyword);
        List<Product> active = normalizedKeyword == null
                ? products.findByStatusOrderByCreatedAtDesc(Product.Status.ACTIVE)
                : products.searchByStatusAndKeyword(Product.Status.ACTIVE, normalizedKeyword);
        Instant now = Instant.now();
        List<Product> result = new ArrayList<>(active.stream()
                .filter(product -> product.getEndTime().isAfter(now))
                .toList());

        if (includeEnded) {
            result.addAll(active.stream()
                    .filter(product -> !product.getEndTime().isAfter(now))
                    .toList());
            List<Product> ended = products.findByStatusOrderByCreatedAtDesc(Product.Status.ENDED);
            List<Product> scheduled = products.findByStatusOrderByCreatedAtDesc(Product.Status.SCHEDULED);
            if (normalizedKeyword != null) {
                String keywordLower = normalizedKeyword.toLowerCase();
                ended = ended.stream()
                        .filter(product -> product.getTitle().toLowerCase().contains(keywordLower))
                        .toList();
                scheduled = scheduled.stream()
                        .filter(product -> product.getTitle().toLowerCase().contains(keywordLower))
                        .toList();
            }
            result.addAll(ended);
            result.addAll(scheduled);
            result.sort(Comparator.comparing(Product::getCreatedAt).reversed());
        }

        return result.stream()
                .map(this::toResponse)
                .toList();
    }

    @Transactional(readOnly = true)
    public ProductResponse getPublicProduct(Long id) {
        Product product = products.findById(id)
                .filter(item -> item.getStatus() == Product.Status.ACTIVE
                        && item.getEndTime().isAfter(Instant.now()))
                .orElseThrow(() -> new AppException(ErrorCode.RESOURCE_NOT_FOUND, "Không tìm thấy sản phẩm"));
        return toResponse(product);
    }

    @Transactional
    public ProductResponse create(String sellerEmail, CreateProductRequest request) {
        if (!request.endTime().isAfter(request.startTime())) {
            throw new AppException(ErrorCode.INVALID_INPUT, "Thời gian kết thúc phải sau thời gian bắt đầu");
        }
        User seller = users.findByEmailIgnoreCase(sellerEmail)
                .orElseThrow(() -> new AppException(ErrorCode.RESOURCE_NOT_FOUND, "Không tìm thấy người bán"));
        Category category = categories.findById(request.categoryId())
                .orElseThrow(() -> new AppException(ErrorCode.RESOURCE_NOT_FOUND, "Không tìm thấy danh mục"));
        Product product = products.save(new Product(seller, category, request.title().trim(),
                request.description(), request.initialPrice(), request.startTime(), request.endTime()));
        return toResponse(product);
    }

    @Transactional(readOnly = true)
    public List<ProductResponse> getSellerProducts(String sellerEmail) {
        return products.findBySellerEmailIgnoreCaseOrderByCreatedAtDesc(sellerEmail)
                .stream().map(this::toResponse).toList();
    }

    @Transactional(readOnly = true)
    public List<ProductResponse> getPendingProducts() {
        return products.findByStatusOrderByCreatedAtAsc(Product.Status.PENDING)
                .stream().map(this::toResponse).toList();
    }

    @Transactional
    public ProductResponse approve(Long id) {
        Product product = findProduct(id);
        if (product.getStatus() != Product.Status.PENDING) {
            throw new AppException(ErrorCode.CONFLICT, "Chỉ có thể duyệt sản phẩm đang chờ duyệt");
        }
        product.approve(Instant.now());
        return toResponse(product);
    }

    @Transactional
    @Scheduled(fixedDelay = 5_000)
    public void activateScheduledProducts() {
        Instant now = Instant.now();
        products.findByStatusAndStartTimeLessThanEqual(Product.Status.SCHEDULED, now)
                .forEach(Product::activate);
    }

    @Transactional
    @Scheduled(fixedDelay = 5_000)
    public void finishExpiredProducts() {
        products.findByStatusAndEndTimeLessThanEqual(Product.Status.ACTIVE, Instant.now())
                .forEach(Product::finish);
    }

    @Transactional
    public ProductResponse cancel(Long id) {
        Product product = findProduct(id);
        if (product.getStatus() != Product.Status.PENDING
                && product.getStatus() != Product.Status.SCHEDULED
                && product.getStatus() != Product.Status.ACTIVE) {
            throw new AppException(ErrorCode.CONFLICT, "Chỉ có thể hủy sản phẩm đang chờ duyệt, đã lên lịch hoặc đang đấu giá");
        }
        product.cancel();
        return toResponse(product);
    }

    private Product findProduct(Long id) {
        return products.findById(id)
                .orElseThrow(() -> new AppException(ErrorCode.RESOURCE_NOT_FOUND, "Không tìm thấy sản phẩm"));
    }

    private ProductResponse toResponse(Product product) {
        return ProductResponse.from(product, bids.countByProduct_Id(product.getId()));
    }

    private String normalizeKeyword(String keyword) {
        if (keyword == null || keyword.isBlank()) return null;
        return keyword.trim();
    }
}
