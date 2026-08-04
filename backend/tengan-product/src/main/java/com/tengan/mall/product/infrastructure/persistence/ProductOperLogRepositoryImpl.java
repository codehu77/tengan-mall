package com.tengan.mall.product.infrastructure.persistence;

import com.tengan.mall.product.domain.model.ProductOperLog;
import com.tengan.mall.product.domain.repository.ProductOperLogRepository;
import java.time.ZoneId;
import org.springframework.stereotype.Repository;

@Repository
public class ProductOperLogRepositoryImpl implements ProductOperLogRepository {

    private final ProductOperLogMapper productOperLogMapper;

    public ProductOperLogRepositoryImpl(ProductOperLogMapper productOperLogMapper) {
        this.productOperLogMapper = productOperLogMapper;
    }

    @Override
    public ProductOperLog save(ProductOperLog operLog) {
        ProductOperLogPO po = new ProductOperLogPO();
        po.setOperator(operLog.getOperator());
        po.setModule(operLog.getModule());
        po.setAction(operLog.getAction());
        po.setTargetDesc(operLog.getTargetDesc());
        po.setCreatedAt(operLog.getCreatedAt().atZone(ZoneId.systemDefault()).toLocalDateTime());
        productOperLogMapper.insert(po);
        operLog.assignId(po.getId());
        return operLog;
    }
}
