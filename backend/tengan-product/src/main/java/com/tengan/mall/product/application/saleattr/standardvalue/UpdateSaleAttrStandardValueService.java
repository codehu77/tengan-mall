package com.tengan.mall.product.application.saleattr.standardvalue;

import com.tengan.mall.product.domain.exception.SaleAttrStandardValueNotFoundException;
import com.tengan.mall.product.domain.model.ProductOperLog;
import com.tengan.mall.product.domain.model.SaleAttrStandardValue;
import com.tengan.mall.product.domain.repository.ProductOperLogRepository;
import com.tengan.mall.product.domain.repository.SaleAttrStandardValueRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/** label/啟用狀態/排序合併成一次 PUT，跟 UpdateSaleAttrService 的做法一致。「刪除」在前端就是呼叫這支把 enabled 設 false。 */
@Service
public class UpdateSaleAttrStandardValueService implements UpdateSaleAttrStandardValueUseCase {

    private final SaleAttrStandardValueRepository standardValueRepository;
    private final ProductOperLogRepository productOperLogRepository;

    public UpdateSaleAttrStandardValueService(SaleAttrStandardValueRepository standardValueRepository,
            ProductOperLogRepository productOperLogRepository) {
        this.standardValueRepository = standardValueRepository;
        this.productOperLogRepository = productOperLogRepository;
    }

    @Override
    @Transactional
    public void update(UpdateSaleAttrStandardValueCommand command) {
        SaleAttrStandardValue value = standardValueRepository.findById(command.id())
                .orElseThrow(() -> new SaleAttrStandardValueNotFoundException(command.id()));
        value.rename(command.label());
        value.updateEnabled(command.enabled());
        value.updateSort(command.sort());
        standardValueRepository.save(value);

        productOperLogRepository.save(ProductOperLog.create(command.operator(), "sale_attr_std_value", "update",
                "修改標準聚合值 " + command.label() + "（id=" + command.id() + "）"));
    }
}
