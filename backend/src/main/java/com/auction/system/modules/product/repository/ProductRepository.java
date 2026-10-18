package com.auction.system.modules.product.repository;

import com.auction.system.modules.product.entity.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface ProductRepository extends JpaRepository<Product, Long> {
    boolean existsByCategoryId(Long categoryId);

    @Query("select p from Product p where p.status = :status " +
            "and (:categoryId is null or p.category.id = :categoryId) " +
            "and (:keyword is null or lower(p.title) like lower(concat('%', :keyword, '%'))) " +
            "order by p.createdAt desc")
    List<Product> search(@Param("status") Product.Status status,
                         @Param("categoryId") Long categoryId,
                         @Param("keyword") String keyword);

    List<Product> findByStatusOrderByCreatedAtAsc(Product.Status status);

    List<Product> findBySellerEmailIgnoreCaseOrderByCreatedAtDesc(String email);
}
