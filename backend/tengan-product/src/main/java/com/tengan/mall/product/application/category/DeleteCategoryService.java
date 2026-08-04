package com.tengan.mall.product.application.category;

import com.tengan.mall.product.domain.exception.CategoryHasChildrenException;
import com.tengan.mall.product.domain.exception.CategoryNotFoundException;
import com.tengan.mall.product.domain.model.ProductOperLog;
import com.tengan.mall.product.domain.repository.CategoryRepository;
import com.tengan.mall.product.domain.repository.ProductOperLogRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class DeleteCategoryService implements DeleteCategoryUseCase {

    private final CategoryRepository categoryRepository;
    private final ProductOperLogRepository productOperLogRepository;

    public DeleteCategoryService(CategoryRepository categoryRepository,
            ProductOperLogRepository productOperLogRepository) {
        this.categoryRepository = categoryRepository;
        this.productOperLogRepository = productOperLogRepository;
    }

    @Override
    @Transactional
    public void delete(DeleteCategoryCommand command) {
        if (!categoryRepository.existsById(command.id())) {
            throw new CategoryNotFoundException(command.id());
        }
        if (categoryRepository.hasChildren(command.id())) {
            throw new CategoryHasChildrenException(command.id());
        }
        categoryRepository.deleteById(command.id());

        productOperLogRepository
                .save(ProductOperLog.create(command.operator(), "category", "delete", "刪除分類 id=" + command.id()));
    }
}
