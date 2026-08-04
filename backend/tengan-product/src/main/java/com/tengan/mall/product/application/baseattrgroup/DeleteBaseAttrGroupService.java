package com.tengan.mall.product.application.baseattrgroup;

import com.tengan.mall.product.domain.exception.BaseAttrGroupNotFoundException;
import com.tengan.mall.product.domain.model.ProductOperLog;
import com.tengan.mall.product.domain.repository.BaseAttrGroupRepository;
import com.tengan.mall.product.domain.repository.ProductOperLogRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class DeleteBaseAttrGroupService implements DeleteBaseAttrGroupUseCase {

    private final BaseAttrGroupRepository baseAttrGroupRepository;
    private final ProductOperLogRepository productOperLogRepository;

    public DeleteBaseAttrGroupService(BaseAttrGroupRepository baseAttrGroupRepository,
            ProductOperLogRepository productOperLogRepository) {
        this.baseAttrGroupRepository = baseAttrGroupRepository;
        this.productOperLogRepository = productOperLogRepository;
    }

    @Override
    @Transactional
    public void delete(DeleteBaseAttrGroupCommand command) {
        if (!baseAttrGroupRepository.existsById(command.id())) {
            throw new BaseAttrGroupNotFoundException(command.id());
        }
        baseAttrGroupRepository.deleteById(command.id());

        productOperLogRepository.save(ProductOperLog.create(command.operator(), "base_attr_group", "delete",
                "刪除規格分組 id=" + command.id()));
    }
}
