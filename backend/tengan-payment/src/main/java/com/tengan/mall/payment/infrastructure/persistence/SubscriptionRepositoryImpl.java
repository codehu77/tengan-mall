package com.tengan.mall.payment.infrastructure.persistence;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.tengan.mall.payment.domain.model.Subscription;
import com.tengan.mall.payment.domain.repository.SubscriptionRepository;
import java.time.Instant;
import java.time.ZoneId;
import java.util.List;
import java.util.Optional;
import org.springframework.stereotype.Repository;

@Repository
public class SubscriptionRepositoryImpl implements SubscriptionRepository {

    private final SubscriptionMapper mapper;

    public SubscriptionRepositoryImpl(SubscriptionMapper mapper) {
        this.mapper = mapper;
    }

    @Override
    public Subscription save(Subscription subscription) {
        SubscriptionPO po = new SubscriptionPO();
        po.setMemberId(subscription.getMemberId());
        // 新建立的訂閱一定「現在算數」，activeSlot=memberId；uk_active_slot 會在這裡擋下同一會員的
        // 併發重複訂閱（見 V3 migration 說明）。
        po.setActiveSlot(subscription.getMemberId());
        po.setTargetTier(subscription.getTargetTier());
        po.setStatus(subscription.getStatus());
        po.setEcpayMerchantTradeNo(subscription.getEcpayMerchantTradeNo());
        po.setPeriodAmount(subscription.getPeriodAmount());
        po.setConsecutiveFailures(subscription.getConsecutiveFailures());
        po.setPaidUntil(toLocalDateTime(subscription.getPaidUntil()));
        po.setBenefitExpiredAt(toLocalDateTime(subscription.getBenefitExpiredAt()));
        po.setCancelledAt(toLocalDateTime(subscription.getCancelledAt()));
        mapper.insert(po);
        subscription.assignId(po.getId());
        return subscription;
    }

    @Override
    public Optional<Subscription> findByMerchantTradeNo(String merchantTradeNo) {
        return Optional
                .ofNullable(mapper.selectOne(new LambdaQueryWrapper<SubscriptionPO>()
                        .eq(SubscriptionPO::getEcpayMerchantTradeNo, merchantTradeNo)))
                .map(this::toDomain);
    }

    @Override
    public Optional<Subscription> findActiveByMemberId(Long memberId) {
        return Optional
                .ofNullable(mapper.selectOne(new LambdaQueryWrapper<SubscriptionPO>()
                        .eq(SubscriptionPO::getMemberId, memberId).eq(SubscriptionPO::getStatus, 1)))
                .map(this::toDomain);
    }

    @Override
    public Optional<Subscription> findCurrentByMemberId(Long memberId) {
        return Optional.ofNullable(mapper.findCurrentByMemberId(memberId)).map(this::toDomain);
    }

    @Override
    public boolean extendPaidUntil(Long id, Instant newPaidUntil) {
        return mapper.extendPaidUntil(id, toLocalDateTime(newPaidUntil)) > 0;
    }

    @Override
    public boolean markCancelled(Long id) {
        return mapper.markCancelled(id) > 0;
    }

    @Override
    public int incrementConsecutiveFailures(Long id) {
        mapper.incrementConsecutiveFailures(id);
        SubscriptionPO po = mapper.selectById(id);
        return po == null ? 0 : po.getConsecutiveFailures();
    }

    @Override
    public List<Subscription> findDueForExpiry(Instant now, int limit) {
        return mapper.findDueForExpiry(toLocalDateTime(now), limit).stream().map(this::toDomain).toList();
    }

    @Override
    public boolean markBenefitExpired(Long id) {
        return mapper.markBenefitExpired(id) > 0;
    }

    private Subscription toDomain(SubscriptionPO po) {
        return Subscription.reconstitute(po.getId(), po.getMemberId(), po.getTargetTier(), po.getStatus(),
                po.getEcpayMerchantTradeNo(), po.getPeriodAmount(), po.getConsecutiveFailures(),
                toInstant(po.getPaidUntil()), toInstant(po.getBenefitExpiredAt()), toInstant(po.getCreatedAt()),
                toInstant(po.getCancelledAt()));
    }

    private java.time.LocalDateTime toLocalDateTime(Instant instant) {
        return instant == null ? null : instant.atZone(ZoneId.systemDefault()).toLocalDateTime();
    }

    private Instant toInstant(java.time.LocalDateTime dateTime) {
        return dateTime == null ? null : dateTime.atZone(ZoneId.systemDefault()).toInstant();
    }
}
