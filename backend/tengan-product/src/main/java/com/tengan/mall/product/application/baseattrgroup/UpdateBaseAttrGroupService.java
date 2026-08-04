package com.tengan.mall.product.application.baseattrgroup;

import com.tengan.mall.product.domain.exception.BaseAttrGroupNotFoundException;
import com.tengan.mall.product.domain.model.BaseAttrGroup;
import com.tengan.mall.product.domain.model.ProductOperLog;
import com.tengan.mall.product.domain.repository.BaseAttrGroupRepository;
import com.tengan.mall.product.domain.repository.ProductOperLogRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class UpdateBaseAttrGroupService implements UpdateBaseAttrGroupUseCase {

    private final BaseAttrGroupRepository baseAttrGroupRepository;
    private final ProductOperLogRepository productOperLogRepository;

    public UpdateBaseAttrGroupService(BaseAttrGroupRepository baseAttrGroupRepository,
            ProductOperLogRepository productOperLogRepository) {
        this.baseAttrGroupRepository = baseAttrGroupRepository;
        this.productOperLogRepository = productOperLogRepository;
    }

    @Override
    @Transactional
    public void update(UpdateBaseAttrGroupCommand command) {
        BaseAttrGroup attrGroup = baseAttrGroupRepository.findById(command.id())
                .orElseThrow(() -> new BaseAttrGroupNotFoundException(command.id()));
        attrGroup.rename(command.name());
        attrGroup.updateSort(command.sort());
        baseAttrGroupRepository.save(attrGroup);

        productOperLogRepository.save(ProductOperLog.create(command.operator(), "base_attr_group", "update",
                "修改規格分組 " + command.name() + "（id=" + command.id() + "）"));
    }
}
