package com.tengan.mall.payment.infrastructure.persistence;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.tengan.mall.payment.domain.model.SubscriptionPayment;
import com.tengan.mall.payment.domain.repository.SubscriptionPaymentRepository;
import java.time.Instant;
import java.time.ZoneId;
import java.util.List;
import org.springframework.stereotype.Repository;

@Repository
public class SubscriptionPaymentRepositoryImpl implements SubscriptionPaymentRepository {

    private final SubscriptionPaymentMapper mapper;

    public SubscriptionPaymentRepositoryImpl(SubscriptionPaymentMapper mapper) {
        this.mapper = mapper;
    }

    @Override
    public SubscriptionPayment save(SubscriptionPayment payment) {
        SubscriptionPaymentPO po = new SubscriptionPaymentPO();
        po.setSubscriptionId(payment.getSubscriptionId());
        po.setGwsr(payment.getGwsr());
        po.setSuccess(payment.isSuccess());
        po.setAmount(payment.getAmount());
        po.setTotalSuccessTimes(payment.getTotalSuccessTimes());
        po.setProcessDate(payment.getProcessDate().atZone(ZoneId.systemDefault()).toLocalDateTime());
        mapper.insert(po);
        payment.assignId(po.getId());
        return payment;
    }

    @Override
    public boolean existsByGwsr(String gwsr) {
        return mapper.selectCount(new LambdaQueryWrapper<SubscriptionPaymentPO>()
                .eq(SubscriptionPaymentPO::getGwsr, gwsr)) > 0;
    }

    @Override
    public List<SubscriptionPayment> findBySubscriptionId(Long subscriptionId) {
        return mapper
                .selectList(new LambdaQueryWrapper<SubscriptionPaymentPO>()
                        .eq(SubscriptionPaymentPO::getSubscriptionId, subscriptionId)
                        .orderByAsc(SubscriptionPaymentPO::getProcessDate))
                .stream().map(this::toDomain).toList();
    }

    private SubscriptionPayment toDomain(SubscriptionPaymentPO po) {
        return SubscriptionPayment.reconstitute(po.getId(), po.getSubscriptionId(), po.getGwsr(), po.getSuccess(),
                po.getAmount(), po.getTotalSuccessTimes(), toInstant(po.getProcessDate()),
                toInstant(po.getCreatedAt()));
    }

    private Instant toInstant(java.time.LocalDateTime dateTime) {
        return dateTime == null ? null : dateTime.atZone(ZoneId.systemDefault()).toInstant();
    }
}
