package com.tengan.mall.product.application.baseattrgroup;

import com.tengan.mall.product.domain.exception.CategoryNotFoundException;
import com.tengan.mall.product.domain.exception.CategoryNotLeafException;
import com.tengan.mall.product.domain.model.BaseAttrGroup;
import com.tengan.mall.product.domain.model.ProductOperLog;
import com.tengan.mall.product.domain.repository.BaseAttrGroupRepository;
import com.tengan.mall.product.domain.repository.CategoryRepository;
import com.tengan.mall.product.domain.repository.ProductOperLogRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class CreateBaseAttrGroupService implements CreateBaseAttrGroupUseCase {

    private final BaseAttrGroupRepository baseAttrGroupRepository;
    private final CategoryRepository categoryRepository;
    private final ProductOperLogRepository productOperLogRepository;

    public CreateBaseAttrGroupService(BaseAttrGroupRepository baseAttrGroupRepository,
            CategoryRepository categoryRepository, ProductOperLogRepository productOperLogRepository) {
        this.baseAttrGroupRepository = baseAttrGroupRepository;
        this.categoryRepository = categoryRepository;
        this.productOperLogRepository = productOperLogRepository;
    }

    @Override
    @Transactional
    public CreateBaseAttrGroupResult create(CreateBaseAttrGroupCommand command) {
        var category = categoryRepository.findById(command.categoryId())
                .orElseThrow(() -> new CategoryNotFoundException(command.categoryId()));
        if (category.getLevel() != 3) {
            throw new CategoryNotLeafException(command.categoryId());
        }

        BaseAttrGroup attrGroup = BaseAttrGroup.create(command.categoryId(), command.name(), command.sort());
        BaseAttrGroup saved = baseAttrGroupRepository.save(attrGroup);

        productOperLogRepository.save(ProductOperLog.create(command.operator(), "base_attr_group", "create",
                "新增規格分組 " + saved.getName() + "（id=" + saved.getId() + "）"));

        return new CreateBaseAttrGroupResult(saved.getId());
    }
}
