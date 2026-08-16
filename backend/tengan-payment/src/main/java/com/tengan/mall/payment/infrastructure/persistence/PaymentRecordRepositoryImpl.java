package com.tengan.mall.payment.infrastructure.persistence;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.tengan.mall.payment.domain.model.PaymentRecord;
import com.tengan.mall.payment.domain.repository.PaymentRecordRepository;
import java.time.ZoneId;
import java.util.List;
import java.util.Optional;
import org.springframework.stereotype.Repository;

@Repository
public class PaymentRecordRepositoryImpl implements PaymentRecordRepository {

    private final PaymentRecordMapper paymentRecordMapper;

    public PaymentRecordRepositoryImpl(PaymentRecordMapper paymentRecordMapper) {
        this.paymentRecordMapper = paymentRecordMapper;
    }

    @Override
    public PaymentRecord save(PaymentRecord record) {
        PaymentRecordPO po = new PaymentRecordPO();
        po.setOrderSn(record.getOrderSn());
        po.setMemberId(record.getMemberId());
        po.setMethod(record.getMethod());
        po.setAmount(record.getAmount());
        po.setStatus(record.getStatus());
        po.setGatewayTradeNo(record.getGatewayTradeNo());
        po.setPaidAt(toLocalDateTime(record.getPaidAt()));
        paymentRecordMapper.insert(po);
        record.assignId(po.getId());
        return record;
    }

    @Override
    public Optional<PaymentRecord> findByOrderSn(String orderSn) {
        return Optional
                .ofNullable(
                        paymentRecordMapper.selectOne(new LambdaQueryWrapper<PaymentRecordPO>()
                                .eq(PaymentRecordPO::getOrderSn, orderSn)))
                .map(this::toDomain);
    }

    @Override
    public boolean markPaid(String orderSn, String gatewayTradeNo) {
        return paymentRecordMapper.markPaid(orderSn, gatewayTradeNo) > 0;
    }

    @Override
    public void deleteIfPending(String orderSn) {
        paymentRecordMapper.deleteIfPending(orderSn);
    }

    @Override
    public List<PaymentRecord> search(String orderSn, String method, int page, int pageSize) {
        Page<PaymentRecordPO> result = paymentRecordMapper.selectPage(new Page<>(page, pageSize),
                buildWrapper(orderSn, method).orderByDesc(PaymentRecordPO::getCreatedAt));
        return result.getRecords().stream().map(this::toDomain).toList();
    }

    @Override
    public long countSearch(String orderSn, String method) {
        return paymentRecordMapper.selectCount(buildWrapper(orderSn, method));
    }

    private LambdaQueryWrapper<PaymentRecordPO> buildWrapper(String orderSn, String method) {
        LambdaQueryWrapper<PaymentRecordPO> wrapper = new LambdaQueryWrapper<>();
        if (orderSn != null && !orderSn.isBlank()) {
            wrapper.eq(PaymentRecordPO::getOrderSn, orderSn);
        }
        if (method != null && !method.isBlank()) {
            wrapper.eq(PaymentRecordPO::getMethod, method);
        }
        return wrapper;
    }

    private PaymentRecord toDomain(PaymentRecordPO po) {
        return PaymentRecord.reconstitute(po.getId(), po.getOrderSn(), po.getMemberId(), po.getMethod(),
                po.getAmount(), po.getStatus(), po.getGatewayTradeNo(), toInstant(po.getPaidAt()),
                toInstant(po.getCreatedAt()));
    }

    private java.time.LocalDateTime toLocalDateTime(java.time.Instant instant) {
        return instant == null ? null : instant.atZone(ZoneId.systemDefault()).toLocalDateTime();
    }

    private java.time.Instant toInstant(java.time.LocalDateTime dateTime) {
        return dateTime == null ? null : dateTime.atZone(ZoneId.systemDefault()).toInstant();
    }
}
