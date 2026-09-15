package com.tengan.mall.product.application.baseattr.standardvalue;

import com.tengan.mall.product.domain.exception.BaseAttrStandardValueNotFoundException;
import com.tengan.mall.product.domain.model.BaseAttrStandardValue;
import com.tengan.mall.product.domain.model.ProductOperLog;
import com.tengan.mall.product.domain.repository.BaseAttrStandardValueRepository;
import com.tengan.mall.product.domain.repository.ProductOperLogRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/** label/啟用狀態/排序合併成一次 PUT，跟 UpdateBaseAttrService 的做法一致。「刪除」在前端就是呼叫這支把 enabled 設 false。 */
@Service
public class UpdateBaseAttrStandardValueService implements UpdateBaseAttrStandardValueUseCase {

    private final BaseAttrStandardValueRepository standardValueRepository;
    private final ProductOperLogRepository productOperLogRepository;

    public UpdateBaseAttrStandardValueService(BaseAttrStandardValueRepository standardValueRepository,
            ProductOperLogRepository productOperLogRepository) {
        this.standardValueRepository = standardValueRepository;
        this.productOperLogRepository = productOperLogRepository;
    }

    @Override
    @Transactional
    public void update(UpdateBaseAttrStandardValueCommand command) {
        BaseAttrStandardValue value = standardValueRepository.findById(command.id())
                .orElseThrow(() -> new BaseAttrStandardValueNotFoundException(command.id()));
        value.rename(command.label());
        value.updateEnabled(command.enabled());
        value.updateSort(command.sort());
        standardValueRepository.save(value);

        productOperLogRepository.save(ProductOperLog.create(command.operator(), "base_attr_std_value", "update",
                "修改標準聚合值 " + command.label() + "（id=" + command.id() + "）"));
    }
}
