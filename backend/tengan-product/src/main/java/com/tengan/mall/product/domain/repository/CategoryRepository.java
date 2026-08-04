package com.tengan.mall.product.domain.repository;

import com.tengan.mall.product.domain.model.Category;
import java.util.List;
import java.util.Optional;

public interface CategoryRepository {

    Category save(Category category);

    Optional<Category> findById(Long id);

    List<Category> findAll();

    boolean existsById(Long id);

    boolean hasChildren(Long parentId);

    void deleteById(Long id);
}
