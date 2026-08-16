package com.tengan.mall.payment.infrastructure.persistence;

import com.tengan.mall.payment.domain.model.PaymentMethodOperLog;
import com.tengan.mall.payment.domain.repository.PaymentMethodOperLogRepository;
import java.time.ZoneId;
import org.springframework.stereotype.Repository;

@Repository
public class PaymentMethodOperLogRepositoryImpl implements PaymentMethodOperLogRepository {

    private final PaymentMethodOperLogMapper mapper;

    public PaymentMethodOperLogRepositoryImpl(PaymentMethodOperLogMapper mapper) {
        this.mapper = mapper;
    }

    @Override
    public PaymentMethodOperLog save(PaymentMethodOperLog operLog) {
        PaymentMethodOperLogPO po = new PaymentMethodOperLogPO();
        po.setOperator(operLog.getOperator());
        po.setModule(operLog.getModule());
        po.setAction(operLog.getAction());
        po.setTargetDesc(operLog.getTargetDesc());
        po.setCreatedAt(operLog.getCreatedAt().atZone(ZoneId.systemDefault()).toLocalDateTime());
        mapper.insert(po);
        operLog.assignId(po.getId());
        return operLog;
    }
}
