package com.auction.system.modules.category.service;

import com.auction.system.exception.ErrorCode;

import com.auction.system.exception.AppException;

import com.auction.system.modules.category.dto.request.CategoryRequest;
import com.auction.system.modules.category.dto.response.CategoryResponse;
import com.auction.system.modules.category.entity.Category;
import com.auction.system.modules.category.repository.CategoryRepository;
import com.auction.system.modules.product.repository.ProductRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class CategoryService {
    private final CategoryRepository categories;
    private final ProductRepository products;

    public CategoryService(CategoryRepository categories, ProductRepository products) {
        this.categories = categories;
        this.products = products;
    }

    @Transactional(readOnly = true)
    public List<CategoryResponse> getAll() {
        return categories.findAll().stream().map(CategoryResponse::from).toList();
    }

    @Transactional(readOnly = true)
    public CategoryResponse getById(Long id) {
        return CategoryResponse.from(findCategory(id));
    }

    @Transactional
    public CategoryResponse create(CategoryRequest request) {
        String name = request.name().trim();
        if (categories.existsByNameIgnoreCase(name)) {
            throw new AppException(ErrorCode.CONFLICT, "Tên danh mục đã tồn tại");
        }
        return CategoryResponse.from(categories.save(new Category(null, name)));
    }

    @Transactional
    public CategoryResponse update(Long id, CategoryRequest request) {
        Category category = findCategory(id);
        String name = request.name().trim();
        if (categories.existsByNameIgnoreCaseAndIdNot(name, id)) {
            throw new AppException(ErrorCode.CONFLICT, "Tên danh mục đã tồn tại");
        }
        category.rename(name);
        return CategoryResponse.from(category);
    }

    @Transactional
    public void delete(Long id) {
        Category category = findCategory(id);
        if (products.existsByCategoryId(id)) {
            throw new AppException(ErrorCode.CONFLICT, "Không thể xóa danh mục đang có sản phẩm");
        }
        categories.delete(category);
    }

    private Category findCategory(Long id) {
        return categories.findById(id)
                .orElseThrow(() -> new AppException(ErrorCode.RESOURCE_NOT_FOUND, "Không tìm thấy danh mục"));
    }
}
