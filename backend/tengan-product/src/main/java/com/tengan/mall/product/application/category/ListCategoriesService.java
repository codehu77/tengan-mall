package com.tengan.mall.product.application.category;

import com.tengan.mall.product.domain.repository.CategoryRepository;
import org.springframework.stereotype.Service;

/** 給 internal/後台用：回傳全部節點，含 HIDDEN 的。 */
@Service
public class ListCategoriesService implements ListCategoriesUseCase {

    private final CategoryRepository categoryRepository;

    public ListCategoriesService(CategoryRepository categoryRepository) {
        this.categoryRepository = categoryRepository;
    }

    @Override
    public ListCategoriesResult list() {
        var items = CategoryTreeAssembler.assemble(categoryRepository.findAll(), false);
        return new ListCategoriesResult(items);
    }
}
