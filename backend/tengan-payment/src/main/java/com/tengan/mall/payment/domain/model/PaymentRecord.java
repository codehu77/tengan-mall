package com.tengan.mall.payment.domain.model;

import java.math.BigDecimal;
import java.time.Instant;

/**
 * 聚合根：一筆訂單的付款記錄。method 是全站定案的付款方式字串（linepay/credit_card/cod，
 * 見 docs/JWT設計.md 付款方式命名定案），不是數字 enum——這個欄位直接對應 tengan-order
 * 的 order.payment_method 快照欄位。
 *
 * <p>gatewayTradeNo 是通用欄位，二選一存進去：ECPay 回填 TradeNo，LINE Pay 回填 transactionId
 * （字串化），COD 沒有金流商所以永遠是 null——一筆付款記錄只會用到其中一種金流，不需要
 * ecpayTradeNo/linePayTransactionId 兩個獨立欄位。</p>
 *
 * <p>狀態轉換（PENDING→PAID）一律走 {@link com.tengan.mall.payment.domain.repository.PaymentRecordRepository#markPaid}
 * 條件式 UPDATE 搶操作權，不透過這裡的 setter/mutate 方法——付款完成通知冪等靠這個機制（比照
 * OrderRepository/MemberCouponRepository 全站既有的「條件式 UPDATE 搶操作權」模式）。</p>
 */
public class PaymentRecord {

    private Long id;
    private final String orderSn;
    private final Long memberId;
    private final String method;
    private final BigDecimal amount;
    private final PaymentStatus status;
    private final String gatewayTradeNo;
    private final Instant paidAt;
    private final Instant createdAt;

    private PaymentRecord(Long id, String orderSn, Long memberId, String method, BigDecimal amount,
            PaymentStatus status, String gatewayTradeNo, Instant paidAt, Instant createdAt) {
        this.id = id;
        this.orderSn = orderSn;
        this.memberId = memberId;
        this.method = method;
        this.amount = amount;
        this.status = status;
        this.gatewayTradeNo = gatewayTradeNo;
        this.paidAt = paidAt;
        this.createdAt = createdAt;
    }

    /** 建立一筆待付款記錄（credit_card/linepay 初始化當下）。 */
    public static PaymentRecord pending(String orderSn, Long memberId, String method, BigDecimal amount,
            String gatewayTradeNo) {
        return new PaymentRecord(null, orderSn, memberId, method, amount, PaymentStatus.PENDING, gatewayTradeNo,
                null, Instant.now());
    }

    /** 建立一筆已付款記錄（COD 同步完成付款）。 */
    public static PaymentRecord paid(String orderSn, Long memberId, String method, BigDecimal amount) {
        Instant now = Instant.now();
        return new PaymentRecord(null, orderSn, memberId, method, amount, PaymentStatus.PAID, null, now, now);
    }

    public static PaymentRecord reconstitute(Long id, String orderSn, Long memberId, String method,
            BigDecimal amount, PaymentStatus status, String gatewayTradeNo, Instant paidAt, Instant createdAt) {
        return new PaymentRecord(id, orderSn, memberId, method, amount, status, gatewayTradeNo, paidAt, createdAt);
    }

    public void assignId(Long id) {
        if (this.id != null) {
            throw new IllegalStateException("PaymentRecord 已經有 id，不可重複指派: " + this.id);
        }
        this.id = id;
    }

    public Long getId() {
        return id;
    }

    public String getOrderSn() {
        return orderSn;
    }

    public Long getMemberId() {
        return memberId;
    }

    public String getMethod() {
        return method;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public PaymentStatus getStatus() {
        return status;
    }

    public String getGatewayTradeNo() {
        return gatewayTradeNo;
    }

    public Instant getPaidAt() {
        return paidAt;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }
}
