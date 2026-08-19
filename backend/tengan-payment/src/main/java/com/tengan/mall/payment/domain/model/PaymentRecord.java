package com.tengan.mall.payment.domain.model;

import java.math.BigDecimal;
import java.time.Instant;

/**
 * 聚合根：一筆訂單的付款嘗試記錄。method 是全站定案的付款方式字串（linepay/credit_card/cod，
 * 見 docs/JWT設計.md 付款方式命名定案），不是數字 enum——這個欄位直接對應 tengan-order
 * 的 order.payment_method 快照欄位。
 *
 * <p>gatewayTradeNo 是通用欄位，二選一存進去：ECPay 回填 TradeNo，LINE Pay 回填 transactionId
 * （字串化），COD 沒有金流商所以永遠是 null——一筆付款記錄只會用到其中一種金流，不需要
 * ecpayTradeNo/linePayTransactionId 兩個獨立欄位。</p>
 *
 * <p>ecpayMerchantTradeNo 只有 credit_card 會用到——ECPay 官方硬規定 MerchantTradeNo 唯一不可重複，
 * 每次付款嘗試都要生成全新一組（仿 Subscription.ecpayMerchantTradeNo 的模式），不能直接拿穩定的
 * orderSn 送出去，否則使用者重試會撞唯一性規則。也因此 orderSn 不再是這張表的唯一鍵——同一張訂單
 * 重試多次會留下多筆歷史記錄（舊的轉 FAILED，不刪除），供之後排程式查帳掃描。</p>
 *
 * <p>狀態轉換（PENDING→PAID/FAILED）一律走 {@link com.tengan.mall.payment.domain.repository.PaymentRecordRepository#markPaid}/
 * {@code markFailed} 條件式 UPDATE 搶操作權，不透過這裡的 setter/mutate 方法——付款完成通知冪等靠這個機制（比照
 * OrderRepository/MemberCouponRepository 全站既有的「條件式 UPDATE 搶操作權」模式）。</p>
 */
public class PaymentRecord {

    private Long id;
    private final String orderSn;
    private final String ecpayMerchantTradeNo;
    private final Long memberId;
    private final String method;
    private final BigDecimal amount;
    private final PaymentStatus status;
    private final String gatewayTradeNo;
    private final Instant paidAt;
    private final Instant createdAt;

    private PaymentRecord(Long id, String orderSn, String ecpayMerchantTradeNo, Long memberId, String method,
            BigDecimal amount, PaymentStatus status, String gatewayTradeNo, Instant paidAt, Instant createdAt) {
        this.id = id;
        this.orderSn = orderSn;
        this.ecpayMerchantTradeNo = ecpayMerchantTradeNo;
        this.memberId = memberId;
        this.method = method;
        this.amount = amount;
        this.status = status;
        this.gatewayTradeNo = gatewayTradeNo;
        this.paidAt = paidAt;
        this.createdAt = createdAt;
    }

    /**
     * 建立一筆待付款記錄（credit_card/linepay 初始化當下）。ecpayMerchantTradeNo 只有 credit_card
     * 會傳非 null；linepay 沒有這個問題（LINE Pay 是 session-based，不會撞唯一性），維持 null。
     */
    public static PaymentRecord pending(String orderSn, Long memberId, String method, BigDecimal amount,
            String ecpayMerchantTradeNo, String gatewayTradeNo) {
        return new PaymentRecord(null, orderSn, ecpayMerchantTradeNo, memberId, method, amount,
                PaymentStatus.PENDING, gatewayTradeNo, null, Instant.now());
    }

    /** 建立一筆已付款記錄（COD 同步完成付款）。 */
    public static PaymentRecord paid(String orderSn, Long memberId, String method, BigDecimal amount) {
        Instant now = Instant.now();
        return new PaymentRecord(null, orderSn, null, memberId, method, amount, PaymentStatus.PAID, null, now, now);
    }

    public static PaymentRecord reconstitute(Long id, String orderSn, String ecpayMerchantTradeNo, Long memberId,
            String method, BigDecimal amount, PaymentStatus status, String gatewayTradeNo, Instant paidAt,
            Instant createdAt) {
        return new PaymentRecord(id, orderSn, ecpayMerchantTradeNo, memberId, method, amount, status,
                gatewayTradeNo, paidAt, createdAt);
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

    public String getEcpayMerchantTradeNo() {
        return ecpayMerchantTradeNo;
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
