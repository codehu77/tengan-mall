package com.tengan.mall.coupon.domain.model;

import java.time.Instant;

/**
 * 聚合根：會員持有的優惠券。consume/revert 不透過這裡的變更方法——核銷/回滾是併發熱點，要條件式
 * UPDATE，不能 load-mutate-save（見 MemberCouponRepository#consume/#revert 的 javadoc）。
 * 這個類別主要服務讀取情境（my/available 列表），useStatus/orderSn 是查詢當下的唯讀快照。
 */
public class MemberCoupon {

    private Long id;
    private final Long templateId;
    private final Long userId;
    private final CouponUseStatus useStatus;
    private final String orderSn;
    private final Instant receivedAt;

    private MemberCoupon(Long id, Long templateId, Long userId, CouponUseStatus useStatus, String orderSn,
            Instant receivedAt) {
        this.id = id;
        this.templateId = templateId;
        this.userId = userId;
        this.useStatus = useStatus;
        this.orderSn = orderSn;
        this.receivedAt = receivedAt;
    }

    public static MemberCoupon grant(Long templateId, Long userId) {
        return new MemberCoupon(null, templateId, userId, CouponUseStatus.UNUSED, null, Instant.now());
    }

    public static MemberCoupon reconstitute(Long id, Long templateId, Long userId, CouponUseStatus useStatus,
            String orderSn, Instant receivedAt) {
        return new MemberCoupon(id, templateId, userId, useStatus, orderSn, receivedAt);
    }

    public void assignId(Long id) {
        if (this.id != null) {
            throw new IllegalStateException("MemberCoupon 已經有 id，不可重複指派: " + this.id);
        }
        this.id = id;
    }

    public Long getId() {
        return id;
    }

    public Long getTemplateId() {
        return templateId;
    }

    public Long getUserId() {
        return userId;
    }

    public CouponUseStatus getUseStatus() {
        return useStatus;
    }

    public String getOrderSn() {
        return orderSn;
    }

    public Instant getReceivedAt() {
        return receivedAt;
    }
}
