package com.tengan.mall.product.application.baseattr;

import com.tengan.mall.product.domain.exception.BaseAttrGroupCategoryMismatchException;
import com.tengan.mall.product.domain.exception.BaseAttrGroupNotFoundException;
import com.tengan.mall.product.domain.exception.CategoryNotFoundException;
import com.tengan.mall.product.domain.exception.CategoryNotLeafException;
import com.tengan.mall.product.domain.model.BaseAttr;
import com.tengan.mall.product.domain.model.ProductOperLog;
import com.tengan.mall.product.domain.repository.BaseAttrGroupRepository;
import com.tengan.mall.product.domain.repository.BaseAttrRepository;
import com.tengan.mall.product.domain.repository.CategoryRepository;
import com.tengan.mall.product.domain.repository.ProductOperLogRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class CreateBaseAttrService implements CreateBaseAttrUseCase {

    private final BaseAttrRepository baseAttrRepository;
    private final BaseAttrGroupRepository baseAttrGroupRepository;
    private final CategoryRepository categoryRepository;
    private final ProductOperLogRepository productOperLogRepository;

    public CreateBaseAttrService(BaseAttrRepository baseAttrRepository,
            BaseAttrGroupRepository baseAttrGroupRepository, CategoryRepository categoryRepository,
            ProductOperLogRepository productOperLogRepository) {
        this.baseAttrRepository = baseAttrRepository;
        this.baseAttrGroupRepository = baseAttrGroupRepository;
        this.categoryRepository = categoryRepository;
        this.productOperLogRepository = productOperLogRepository;
    }

    @Override
    @Transactional
    public CreateBaseAttrResult create(CreateBaseAttrCommand command) {
        var category = categoryRepository.findById(command.categoryId())
                .orElseThrow(() -> new CategoryNotFoundException(command.categoryId()));
        if (category.getLevel() != 3) {
            throw new CategoryNotLeafException(command.categoryId());
        }

        var attrGroup = baseAttrGroupRepository.findById(command.attrGroupId())
                .orElseThrow(() -> new BaseAttrGroupNotFoundException(command.attrGroupId()));
        if (!attrGroup.getCategoryId().equals(command.categoryId())) {
            throw new BaseAttrGroupCategoryMismatchException(command.attrGroupId(), command.categoryId());
        }

        BaseAttr attr = BaseAttr.create(command.categoryId(), command.attrGroupId(), command.name(),
                command.searchable(), command.sort());
        BaseAttr saved = baseAttrRepository.save(attr);

        productOperLogRepository.save(ProductOperLog.create(command.operator(), "base_attr", "create",
                "新增規格參數 " + saved.getName() + "（id=" + saved.getId() + "）"));

        return new CreateBaseAttrResult(saved.getId());
    }
}
