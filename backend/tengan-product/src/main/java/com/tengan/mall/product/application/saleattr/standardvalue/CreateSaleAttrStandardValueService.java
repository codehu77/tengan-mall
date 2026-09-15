package com.tengan.mall.product.application.saleattr.standardvalue;

import com.tengan.mall.product.domain.exception.SaleAttrNotFoundException;
import com.tengan.mall.product.domain.model.ProductOperLog;
import com.tengan.mall.product.domain.model.SaleAttrStandardValue;
import com.tengan.mall.product.domain.repository.ProductOperLogRepository;
import com.tengan.mall.product.domain.repository.SaleAttrRepository;
import com.tengan.mall.product.domain.repository.SaleAttrStandardValueRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class CreateSaleAttrStandardValueService implements CreateSaleAttrStandardValueUseCase {

    private final SaleAttrStandardValueRepository standardValueRepository;
    private final SaleAttrRepository saleAttrRepository;
    private final ProductOperLogRepository productOperLogRepository;

    public CreateSaleAttrStandardValueService(SaleAttrStandardValueRepository standardValueRepository,
            SaleAttrRepository saleAttrRepository, ProductOperLogRepository productOperLogRepository) {
        this.standardValueRepository = standardValueRepository;
        this.saleAttrRepository = saleAttrRepository;
        this.productOperLogRepository = productOperLogRepository;
    }

    @Override
    @Transactional
    public CreateSaleAttrStandardValueResult create(CreateSaleAttrStandardValueCommand command) {
        saleAttrRepository.findById(command.attrId())
                .orElseThrow(() -> new SaleAttrNotFoundException(command.attrId()));

        SaleAttrStandardValue value = SaleAttrStandardValue.create(command.attrId(), command.label(), command.sort());
        SaleAttrStandardValue saved = standardValueRepository.save(value);

        productOperLogRepository.save(ProductOperLog.create(command.operator(), "sale_attr_std_value", "create",
                "新增標準聚合值 " + saved.getLabel() + "（id=" + saved.getId() + "，attrId=" + saved.getAttrId() + "）"));

        return new CreateSaleAttrStandardValueResult(saved.getId());
    }
}
