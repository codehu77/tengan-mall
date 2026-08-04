package com.tengan.mall.product.application.category;

import com.tengan.mall.product.domain.exception.CategoryNotFoundException;
import com.tengan.mall.product.domain.model.Category;
import com.tengan.mall.product.domain.model.ProductOperLog;
import com.tengan.mall.product.domain.repository.CategoryRepository;
import com.tengan.mall.product.domain.repository.ProductOperLogRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class UpdateCategoryService implements UpdateCategoryUseCase {

    private final CategoryRepository categoryRepository;
    private final ProductOperLogRepository productOperLogRepository;

    public UpdateCategoryService(CategoryRepository categoryRepository,
            ProductOperLogRepository productOperLogRepository) {
        this.categoryRepository = categoryRepository;
        this.productOperLogRepository = productOperLogRepository;
    }

    @Override
    @Transactional
    public void update(UpdateCategoryCommand command) {
        Category category = categoryRepository.findById(command.id())
                .orElseThrow(() -> new CategoryNotFoundException(command.id()));
        category.rename(command.name());
        category.updateIcon(command.icon());
        category.updateSort(command.sort());
        categoryRepository.save(category);

        productOperLogRepository.save(ProductOperLog.create(command.operator(), "category", "update",
                "修改分類 " + command.name() + "（id=" + command.id() + "）"));
    }
}
