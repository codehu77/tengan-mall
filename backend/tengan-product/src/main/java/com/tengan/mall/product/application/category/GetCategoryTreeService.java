package com.tengan.mall.product.application.category;

import com.tengan.mall.product.domain.repository.CategoryRepository;
import org.springframework.stereotype.Service;

/** 給 public 端點用：只回傳 status=VISIBLE 的節點。 */
@Service
public class GetCategoryTreeService implements GetCategoryTreeUseCase {

    private final CategoryRepository categoryRepository;

    public GetCategoryTreeService(CategoryRepository categoryRepository) {
        this.categoryRepository = categoryRepository;
    }

    @Override
    public GetCategoryTreeResult tree() {
        var items = CategoryTreeAssembler.assemble(categoryRepository.findAll(), true);
        return new GetCategoryTreeResult(items);
    }
}
