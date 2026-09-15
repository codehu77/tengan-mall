package com.tengan.mall.product.application.baseattr.standardvalue;

import com.tengan.mall.product.domain.exception.BaseAttrNotFoundException;
import com.tengan.mall.product.domain.model.BaseAttrStandardValue;
import com.tengan.mall.product.domain.model.ProductOperLog;
import com.tengan.mall.product.domain.repository.BaseAttrRepository;
import com.tengan.mall.product.domain.repository.BaseAttrStandardValueRepository;
import com.tengan.mall.product.domain.repository.ProductOperLogRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class CreateBaseAttrStandardValueService implements CreateBaseAttrStandardValueUseCase {

    private final BaseAttrStandardValueRepository standardValueRepository;
    private final BaseAttrRepository baseAttrRepository;
    private final ProductOperLogRepository productOperLogRepository;

    public CreateBaseAttrStandardValueService(BaseAttrStandardValueRepository standardValueRepository,
            BaseAttrRepository baseAttrRepository, ProductOperLogRepository productOperLogRepository) {
        this.standardValueRepository = standardValueRepository;
        this.baseAttrRepository = baseAttrRepository;
        this.productOperLogRepository = productOperLogRepository;
    }

    @Override
    @Transactional
    public CreateBaseAttrStandardValueResult create(CreateBaseAttrStandardValueCommand command) {
        baseAttrRepository.findById(command.attrId())
                .orElseThrow(() -> new BaseAttrNotFoundException(command.attrId()));

        BaseAttrStandardValue value = BaseAttrStandardValue.create(command.attrId(), command.label(), command.sort());
        BaseAttrStandardValue saved = standardValueRepository.save(value);

        productOperLogRepository.save(ProductOperLog.create(command.operator(), "base_attr_std_value", "create",
                "新增標準聚合值 " + saved.getLabel() + "（id=" + saved.getId() + "，attrId=" + saved.getAttrId() + "）"));

        return new CreateBaseAttrStandardValueResult(saved.getId());
    }
}
