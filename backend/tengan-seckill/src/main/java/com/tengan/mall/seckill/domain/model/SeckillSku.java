package com.tengan.mall.seckill.domain.model;

import java.math.BigDecimal;
import java.time.Instant;

/**
 * 一場活動底下的一顆秒殺 SKU。{@code soldCount}/{@code settledAt} 是結算後才回填的欄位——
 * {@code settledAt IS NULL} 是結算冪等的判斷依據（見 {@link com.tengan.mall.seckill.domain.repository.SeckillSkuRepository#settle}
 * 的條件式 UPDATE），比照 db_design_conventions 記憶的既有慣例，不用開流水帳表。
 */
public class SeckillSku {

    private Long id;
    private final Long activityId;
    private final Long skuId;
    private final BigDecimal seckillPrice;
    private final int seckillCount;
    private final int limitPerUser;
    private int soldCount;
    private Instant settledAt;
    private final Instant createdAt;

    private SeckillSku(Long id, Long activityId, Long skuId, BigDecimal seckillPrice, int seckillCount,
            int limitPerUser, int soldCount, Instant settledAt, Instant createdAt) {
        this.id = id;
        this.activityId = activityId;
        this.skuId = skuId;
        this.seckillPrice = seckillPrice;
        this.seckillCount = seckillCount;
        this.limitPerUser = limitPerUser;
        this.soldCount = soldCount;
        this.settledAt = settledAt;
        this.createdAt = createdAt;
    }

    public static SeckillSku create(Long activityId, Long skuId, BigDecimal seckillPrice, int seckillCount,
            int limitPerUser) {
        if (activityId == null || skuId == null) {
            throw new IllegalArgumentException("activityId/skuId 不可為 null");
        }
        if (seckillPrice == null || seckillPrice.signum() < 0) {
            throw new IllegalArgumentException("seckillPrice 不可為負數");
        }
        if (seckillCount <= 0) {
            throw new IllegalArgumentException("seckillCount 必須大於 0");
        }
        if (limitPerUser <= 0) {
            throw new IllegalArgumentException("limitPerUser 必須大於 0");
        }
        return new SeckillSku(null, activityId, skuId, seckillPrice, seckillCount, limitPerUser, 0, null,
                Instant.now());
    }

    public static SeckillSku reconstitute(Long id, Long activityId, Long skuId, BigDecimal seckillPrice,
            int seckillCount, int limitPerUser, int soldCount, Instant settledAt, Instant createdAt) {
        return new SeckillSku(id, activityId, skuId, seckillPrice, seckillCount, limitPerUser, soldCount, settledAt,
                createdAt);
    }

    public void assignId(Long id) {
        if (this.id != null) {
            throw new IllegalStateException("SeckillSku 已經有 id，不可重複指派: " + this.id);
        }
        this.id = id;
    }

    public Long getId() {
        return id;
    }

    public Long getActivityId() {
        return activityId;
    }

    public Long getSkuId() {
        return skuId;
    }

    public BigDecimal getSeckillPrice() {
        return seckillPrice;
    }

    public int getSeckillCount() {
        return seckillCount;
    }

    public int getLimitPerUser() {
        return limitPerUser;
    }

    public int getSoldCount() {
        return soldCount;
    }

    public Instant getSettledAt() {
        return settledAt;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }
}
