package com.tengan.mall.order.domain.model;

import java.math.BigDecimal;

/** 單列設定表（比照 tengan-wallet 的 WalletRule），id 固定為 1。 */
public class FreightRule {

    private final BigDecimal freeShippingThreshold;
    private final BigDecimal shippingFee;

    public FreightRule(BigDecimal freeShippingThreshold, BigDecimal shippingFee) {
        this.freeShippingThreshold = freeShippingThreshold;
        this.shippingFee = shippingFee;
    }

    /** 商品原價小計（折扣前）達門檻免運，否則收取設定的運費。 */
    public BigDecimal computeFee(BigDecimal subtotal) {
        return subtotal.compareTo(freeShippingThreshold) >= 0 ? BigDecimal.ZERO : shippingFee;
    }

    public BigDecimal getFreeShippingThreshold() {
        return freeShippingThreshold;
    }

    public BigDecimal getShippingFee() {
        return shippingFee;
    }
}
