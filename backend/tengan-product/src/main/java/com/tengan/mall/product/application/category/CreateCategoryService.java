package com.tengan.mall.product.application.category;

import com.tengan.mall.product.domain.exception.CategoryLevelLimitExceededException;
import com.tengan.mall.product.domain.exception.CategoryNotFoundException;
import com.tengan.mall.product.domain.model.Category;
import com.tengan.mall.product.domain.model.ProductOperLog;
import com.tengan.mall.product.domain.repository.CategoryRepository;
import com.tengan.mall.product.domain.repository.ProductOperLogRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class CreateCategoryService implements CreateCategoryUseCase {

    private final CategoryRepository categoryRepository;
    private final ProductOperLogRepository productOperLogRepository;

    public CreateCategoryService(CategoryRepository categoryRepository,
            ProductOperLogRepository productOperLogRepository) {
        this.categoryRepository = categoryRepository;
        this.productOperLogRepository = productOperLogRepository;
    }

    @Override
    @Transactional
    public CreateCategoryResult create(CreateCategoryCommand command) {
        int level = resolveLevel(command.parentId());
        Category category = Category.create(command.parentId(), command.name(), command.icon(), command.sort(),
                level);
        Category saved = categoryRepository.save(category);

        productOperLogRepository.save(ProductOperLog.create(command.operator(), "category", "create",
                "新增分類 " + saved.getName() + "（id=" + saved.getId() + "）"));

        return new CreateCategoryResult(saved.getId());
    }

    private int resolveLevel(Long parentId) {
        if (parentId == null || parentId == 0L) {
            return 1;
        }
        Category parent = categoryRepository.findById(parentId)
                .orElseThrow(() -> new CategoryNotFoundException(parentId));
        if (parent.getLevel() >= 3) {
            throw new CategoryLevelLimitExceededException(parentId);
        }
        return parent.getLevel() + 1;
    }
}
