package com.tengan.mall.payment.domain.repository;

import com.tengan.mall.payment.domain.model.Subscription;
import java.time.Instant;
import java.util.List;
import java.util.Optional;

public interface SubscriptionRepository {

    Subscription save(Subscription subscription);

    Optional<Subscription> findByMerchantTradeNo(String merchantTradeNo);

    /** status=ACTIVE 限定，只有 ACTIVE 的訂閱能被取消。 */
    Optional<Subscription> findActiveByMemberId(Long memberId);

    /**
     * 查這個會員「目前還算數」的最新一份訂閱——不限狀態（ACTIVE 或 CANCELLED 但還在鑑賞期都算），
     * 只要 benefitExpiredAt 還沒被排程標記過就算還算數。供 GetMySubscriptionService 顯示狀態、
     * SubscribeService 判斷「還有沒有進行中的訂閱不能重複訂閱」共用。
     */
    Optional<Subscription> findCurrentByMemberId(Long memberId);

    /** 條件式 UPDATE：延長到期日、重置失敗計數。扣款成功時呼叫。 */
    boolean extendPaidUntil(Long id, Instant newPaidUntil);

    /** 條件式 UPDATE status=CANCELLED WHERE id=? AND status=ACTIVE，不動 paidUntil，回傳是否真的搶到操作權。 */
    boolean markCancelled(Long id);

    /**
     * PENDING 且從未成功過的訂閱判定作廢：立即轉 CANCELLED + 清空 active_slot + 設 benefitExpiredAt=NOW()
     * （沒有真的授予過權益，不需要真的呼叫 wallet 降級），讓會員能立刻重新訂閱，不用等
     * SubscriptionExpiryScheduler 下一輪（最長 1 小時）才處理。條件式 UPDATE WHERE status=PENDING，
     * 回傳是否真的搶到操作權。
     */
    boolean abandonPending(Long id);

    /** 遞增連續失敗次數，回傳遞增後的次數。 */
    int incrementConsecutiveFailures(Long id);

    /**
     * status=CANCELLED AND paidUntil<=now AND benefitExpiredAt IS NULL，供 SubscriptionExpiryScheduler 掃描。
     * 限定 CANCELLED 是因為 ACTIVE 代表還在繼續扣款，paidUntil 過期只是這期通知還沒送達，不代表真的到期。
     */
    List<Subscription> findDueForExpiry(Instant now, int limit);

    /** 條件式 UPDATE benefit_expired_at=NOW() WHERE id=? AND benefit_expired_at IS NULL，冪等防重複降級。 */
    boolean markBenefitExpired(Long id);

    /**
     * status=PENDING(0) 且 created_at<=cutoff，供排程式查帳/手動立即查帳掃描候選（Phase 8.6 原本設計的
     * 情境 A，訂閱版本）。排程呼叫時 cutoff 傳「現在-40分鐘」，手動立即查帳呼叫時 cutoff 傳「現在」。
     */
    List<Subscription> findStuckPending(Instant cutoff, int limit);

    /**
     * status=ACTIVE(1) 且 paid_until<=cutoff，供排程式查帳/手動立即查帳掃描候選（情境 B：續期通知
     * 完全沒來）。排程呼叫時 cutoff 傳「現在-寬限緩衝」（避免誤判 PeriodReturnURL 正常延遲送達的情況），
     * 手動立即查帳呼叫時 cutoff 傳「現在」。
     */
    List<Subscription> findStuckActive(Instant cutoff, int limit);
}
