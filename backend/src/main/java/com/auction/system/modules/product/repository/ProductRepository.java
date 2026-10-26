package com.auction.system.modules.product.repository;

import com.auction.system.modules.product.entity.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.time.Instant;

public interface ProductRepository extends JpaRepository<Product, Long> {
    boolean existsByCategoryId(Long categoryId);

    @Query("select p from Product p where p.status = :status " +
            "and lower(p.title) like lower(concat('%', :keyword, '%')) " +
            "order by p.createdAt desc")
    List<Product> searchByStatusAndKeyword(@Param("status") Product.Status status,
                                           @Param("keyword") String keyword);

    List<Product> findByStatusOrderByCreatedAtDesc(Product.Status status);

    List<Product> findByStatusOrderByCreatedAtAsc(Product.Status status);

    List<Product> findByStatusAndStartTimeLessThanEqual(Product.Status status, Instant startTime);

    List<Product> findByStatusAndEndTimeLessThanEqual(Product.Status status, Instant endTime);

    List<Product> findBySellerEmailIgnoreCaseOrderByCreatedAtDesc(String email);
}
