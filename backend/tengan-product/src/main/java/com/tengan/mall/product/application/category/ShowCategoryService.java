package com.tengan.mall.product.application.category;

import com.tengan.mall.product.domain.exception.CategoryNotFoundException;
import com.tengan.mall.product.domain.model.Category;
import com.tengan.mall.product.domain.model.ProductOperLog;
import com.tengan.mall.product.domain.repository.CategoryRepository;
import com.tengan.mall.product.domain.repository.ProductOperLogRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class ShowCategoryService implements ShowCategoryUseCase {

    private final CategoryRepository categoryRepository;
    private final ProductOperLogRepository productOperLogRepository;

    public ShowCategoryService(CategoryRepository categoryRepository,
            ProductOperLogRepository productOperLogRepository) {
        this.categoryRepository = categoryRepository;
        this.productOperLogRepository = productOperLogRepository;
    }

    @Override
    @Transactional
    public void show(ShowCategoryCommand command) {
        Category category = categoryRepository.findById(command.id())
                .orElseThrow(() -> new CategoryNotFoundException(command.id()));
        category.show();
        categoryRepository.save(category);

        productOperLogRepository
                .save(ProductOperLog.create(command.operator(), "category", "show", "顯示分類 id=" + command.id()));
    }
}
