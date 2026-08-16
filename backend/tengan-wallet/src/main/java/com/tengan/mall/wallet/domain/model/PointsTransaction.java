package com.tengan.mall.wallet.domain.model;

import java.time.Instant;

/**
 * 聚合根：一筆點數帳本異動。`type` 是不可變的起因（為什麼有這筆錢動了），`status` 只描述兩種生命週期：
 * EARN 的 reserve（PENDING）→confirm（CONFIRMED），跟 REDEEM 被取消訂單撤銷（CONFIRMED→REVERSED，
 * 條件式 UPDATE，比照 tengan-coupon MemberCoupon 的 consume/revert）。EXPIRE/ADJUST 一律直接以
 * CONFIRMED 寫入、終生不轉換。
 *
 * <p>到期機制：365 天到期不是把原本的 EARN 列狀態改掉，而是另開一筆負向 `EXPIRE` 列沖銷（`relatedTransactionId`
 * 指回原本的 EARN 列 id，供排程判斷「這筆還沒沖銷過」），讓交易明細能看到「哪一筆到期了」這個獨立事件，
 * 原始 EARN 列則永遠保留「當初真的賺到過這些點數」的歷史事實。</p>
 *
 * <p>可用餘額 = SUM(points) WHERE member_id=? AND status=CONFIRMED——EARN(正)/REDEEM(負，未撤銷)/
 * EXPIRE(負)/ADJUST(正負皆可) 全部自然相加即為正確餘額，REVERSED 狀態的 REDEEM 列因為不是 CONFIRMED
 * 而自動被排除在外，不需要另外查詢沖正。</p>
 */
public class PointsTransaction {

    private Long id;
    private final Long memberId;
    private final PointsTransactionType type;
    private PointsTransactionStatus status;
    private int points;
    private Integer balanceAfter;
    private final String title;
    private final String description;
    private final String orderSn;
    private final String channel;
    private final String operator;
    private final Instant createdAt;
    private Instant expiresAt;
    private final Long relatedTransactionId;

    private PointsTransaction(Long id, Long memberId, PointsTransactionType type, PointsTransactionStatus status,
            int points, Integer balanceAfter, String title, String description, String orderSn, String channel,
            String operator, Instant createdAt, Instant expiresAt, Long relatedTransactionId) {
        this.id = id;
        this.memberId = memberId;
        this.type = type;
        this.status = status;
        this.points = points;
        this.balanceAfter = balanceAfter;
        this.title = title;
        this.description = description;
        this.orderSn = orderSn;
        this.channel = channel;
        this.operator = operator;
        this.createdAt = createdAt;
        this.expiresAt = expiresAt;
        this.relatedTransactionId = relatedTransactionId;
    }

    /** 訂單確認收貨當下的非關鍵路徑呼叫——先掛一筆 PENDING，讓「待入帳點數」在鑑賞期內就看得到。 */
    public static PointsTransaction reserve(Long memberId, int points, String orderSn, String title,
            String description) {
        return new PointsTransaction(null, memberId, PointsTransactionType.EARN, PointsTransactionStatus.PENDING,
                points, null, title, description, orderSn, "ORDER_COMPLETION", null, Instant.now(), null, null);
    }

    /** reserve 呼叫失敗過、7 天後排程找不到對應 PENDING 列時的補建路徑，直接以 CONFIRMED 入帳。 */
    public static PointsTransaction earnDirect(Long memberId, int points, String orderSn, String title,
            String description, Instant expiresAt) {
        return new PointsTransaction(null, memberId, PointsTransactionType.EARN, PointsTransactionStatus.CONFIRMED,
                points, null, title, description, orderSn, "ORDER_COMPLETION", null, Instant.now(), expiresAt, null);
    }

    public static PointsTransaction redeem(Long memberId, int points, String orderSn, String title,
            String description) {
        return new PointsTransaction(null, memberId, PointsTransactionType.REDEEM, PointsTransactionStatus.CONFIRMED,
                -Math.abs(points), null, title, description, orderSn, "CHECKOUT", null, Instant.now(), null, null);
    }

    public static PointsTransaction expire(Long memberId, int points, Long relatedTransactionId, String title,
            String description) {
        return new PointsTransaction(null, memberId, PointsTransactionType.EXPIRE, PointsTransactionStatus.CONFIRMED,
                -Math.abs(points), null, title, description, null, "SYSTEM", null, Instant.now(), null,
                relatedTransactionId);
    }

    public static PointsTransaction adjust(Long memberId, int points, String title, String description,
            String operator) {
        return new PointsTransaction(null, memberId, PointsTransactionType.ADJUST, PointsTransactionStatus.CONFIRMED,
                points, null, title, description, null, "ADMIN_ADJUST", operator, Instant.now(), null, null);
    }

    public static PointsTransaction reconstitute(Long id, Long memberId, PointsTransactionType type,
            PointsTransactionStatus status, int points, Integer balanceAfter, String title, String description,
            String orderSn, String channel, String operator, Instant createdAt, Instant expiresAt,
            Long relatedTransactionId) {
        return new PointsTransaction(id, memberId, type, status, points, balanceAfter, title, description, orderSn,
                channel, operator, createdAt, expiresAt, relatedTransactionId);
    }

    public void assignId(Long id) {
        if (this.id != null) {
            throw new IllegalStateException("PointsTransaction 已經有 id，不可重複指派: " + this.id);
        }
        this.id = id;
    }

    public void assignBalanceAfter(int balanceAfter) {
        this.balanceAfter = balanceAfter;
    }

    public Long getId() {
        return id;
    }

    public Long getMemberId() {
        return memberId;
    }

    public PointsTransactionType getType() {
        return type;
    }

    public PointsTransactionStatus getStatus() {
        return status;
    }

    public int getPoints() {
        return points;
    }

    public Integer getBalanceAfter() {
        return balanceAfter;
    }

    public String getTitle() {
        return title;
    }

    public String getDescription() {
        return description;
    }

    public String getOrderSn() {
        return orderSn;
    }

    public String getChannel() {
        return channel;
    }

    public String getOperator() {
        return operator;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }

    public Instant getExpiresAt() {
        return expiresAt;
    }

    public Long getRelatedTransactionId() {
        return relatedTransactionId;
    }
}
