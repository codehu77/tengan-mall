package com.tengan.mall.product.application.category;

import com.tengan.mall.product.domain.exception.CategoryNotFoundException;
import com.tengan.mall.product.domain.model.Category;
import com.tengan.mall.product.domain.model.ProductOperLog;
import com.tengan.mall.product.domain.repository.CategoryRepository;
import com.tengan.mall.product.domain.repository.ProductOperLogRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class HideCategoryService implements HideCategoryUseCase {

    private final CategoryRepository categoryRepository;
    private final ProductOperLogRepository productOperLogRepository;

    public HideCategoryService(CategoryRepository categoryRepository,
            ProductOperLogRepository productOperLogRepository) {
        this.categoryRepository = categoryRepository;
        this.productOperLogRepository = productOperLogRepository;
    }

    @Override
    @Transactional
    public void hide(HideCategoryCommand command) {
        Category category = categoryRepository.findById(command.id())
                .orElseThrow(() -> new CategoryNotFoundException(command.id()));
        category.hide();
        categoryRepository.save(category);

        productOperLogRepository
                .save(ProductOperLog.create(command.operator(), "category", "hide", "隱藏分類 id=" + command.id()));
    }
}
