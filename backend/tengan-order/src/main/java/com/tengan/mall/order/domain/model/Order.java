package com.tengan.mall.order.domain.model;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;

/**
 * 聚合根：訂單。root+child 模式（比照 WareOrderTask/PurchaseOrder）——建立時整張連明細一起寫入，
 * 之後狀態轉換一律走 OrderRepository 的條件式 UPDATE（markCancelled/markPaid/markShipped/
 * markCompleted），不走 load-mutate-save，這個 domain 物件本身沒有 cancel()/pay() 這類方法
 * （跟 WareOrderTask 沒有 markReleased() domain 方法是同一個模式，見規劃文件三、）。
 */
public class Order {

    private Long id;
    private final String orderSn;
    private final Long memberId;
    private OrderStatus status;
    private String cancelReason;
    private final BigDecimal totalAmount;
    private final BigDecimal discountAmount;
    private final BigDecimal payAmount;
    private final String paymentMethod;
    private final Long couponId;
    private final String receiverName;
    private final String receiverPhone;
    private final String city;
    private final String district;
    private final String postalCode;
    private final String street;
    private final String remark;
    private Instant receiptTime;
    private final Instant createdAt;
    private final List<OrderItem> items;

    private Order(Long id, String orderSn, Long memberId, OrderStatus status, String cancelReason,
            BigDecimal totalAmount, BigDecimal discountAmount, BigDecimal payAmount, String paymentMethod,
            Long couponId, String receiverName, String receiverPhone, String city, String district,
            String postalCode, String street, String remark, Instant receiptTime, Instant createdAt,
            List<OrderItem> items) {
        this.id = id;
        this.orderSn = orderSn;
        this.memberId = memberId;
        this.status = status;
        this.cancelReason = cancelReason;
        this.totalAmount = totalAmount;
        this.discountAmount = discountAmount;
        this.payAmount = payAmount;
        this.paymentMethod = paymentMethod;
        this.couponId = couponId;
        this.receiverName = receiverName;
        this.receiverPhone = receiverPhone;
        this.city = city;
        this.district = district;
        this.postalCode = postalCode;
        this.street = street;
        this.remark = remark;
        this.receiptTime = receiptTime;
        this.createdAt = createdAt;
        this.items = items;
    }

    public static Order create(String orderSn, Long memberId, String paymentMethod, Long couponId,
            String receiverName, String receiverPhone, String city, String district, String postalCode,
            String street, String remark, BigDecimal totalAmount, BigDecimal discountAmount, BigDecimal payAmount,
            List<OrderItem> items) {
        return new Order(null, orderSn, memberId, OrderStatus.PENDING_PAYMENT, null, totalAmount, discountAmount,
                payAmount, paymentMethod, couponId, receiverName, receiverPhone, city, district, postalCode, street,
                remark, null, Instant.now(), items);
    }

    public static Order reconstitute(Long id, String orderSn, Long memberId, OrderStatus status,
            String cancelReason, BigDecimal totalAmount, BigDecimal discountAmount, BigDecimal payAmount,
            String paymentMethod, Long couponId, String receiverName, String receiverPhone, String city,
            String district, String postalCode, String street, String remark, Instant receiptTime,
            Instant createdAt, List<OrderItem> items) {
        return new Order(id, orderSn, memberId, status, cancelReason, totalAmount, discountAmount, payAmount,
                paymentMethod, couponId, receiverName, receiverPhone, city, district, postalCode, street, remark,
                receiptTime, createdAt, items);
    }

    public void assignId(Long id) {
        if (this.id != null) {
            throw new IllegalStateException("Order 已經有 id，不可重複指派: " + this.id);
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

    public OrderStatus getStatus() {
        return status;
    }

    public String getCancelReason() {
        return cancelReason;
    }

    public BigDecimal getTotalAmount() {
        return totalAmount;
    }

    public BigDecimal getDiscountAmount() {
        return discountAmount;
    }

    public BigDecimal getPayAmount() {
        return payAmount;
    }

    public String getPaymentMethod() {
        return paymentMethod;
    }

    public Long getCouponId() {
        return couponId;
    }

    public String getReceiverName() {
        return receiverName;
    }

    public String getReceiverPhone() {
        return receiverPhone;
    }

    public String getCity() {
        return city;
    }

    public String getDistrict() {
        return district;
    }

    public String getPostalCode() {
        return postalCode;
    }

    public String getStreet() {
        return street;
    }

    public String getRemark() {
        return remark;
    }

    public Instant getReceiptTime() {
        return receiptTime;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }

    public List<OrderItem> getItems() {
        return items;
    }
}
