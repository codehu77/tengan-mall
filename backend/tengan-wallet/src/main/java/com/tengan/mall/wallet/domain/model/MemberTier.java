package com.tengan.mall.wallet.domain.model;

import java.time.Instant;

/**
 * 聚合根：會員目前等級。lazy-create——查無資料視同 FREE（見 MemberTierRepository#findByMemberId），
 * 不需要消費 tengan-auth 的 member.registered 事件預建每個會員一筆。狀態轉換一律走 Repository
 * 的 upsert（MemberTierRepository#upsert），不走 load-mutate-save。
 *
 * <p>Phase 8.5（訂閱）上線前，PRO/PRO+ 只能靠後台手動調整（見 UpdateMemberTierService）——這是
 * 目前唯一的升等入口，跟點數 ADJUST「客服補償」是同一種精神。</p>
 */
public class MemberTier {

    private final Long memberId;
    private final MemberTierLevel tier;
    private final String updatedBy;
    private final String updatedReason;
    private final Instant updatedAt;

    private MemberTier(Long memberId, MemberTierLevel tier, String updatedBy, String updatedReason,
            Instant updatedAt) {
        this.memberId = memberId;
        this.tier = tier;
        this.updatedBy = updatedBy;
        this.updatedReason = updatedReason;
        this.updatedAt = updatedAt;
    }

    public static MemberTier reconstitute(Long memberId, MemberTierLevel tier, String updatedBy,
            String updatedReason, Instant updatedAt) {
        return new MemberTier(memberId, tier, updatedBy, updatedReason, updatedAt);
    }

    public static MemberTier defaultFree(Long memberId) {
        return new MemberTier(memberId, MemberTierLevel.FREE, null, null, null);
    }

    public Long getMemberId() {
        return memberId;
    }

    public MemberTierLevel getTier() {
        return tier;
    }

    public String getUpdatedBy() {
        return updatedBy;
    }

    public String getUpdatedReason() {
        return updatedReason;
    }

    public Instant getUpdatedAt() {
        return updatedAt;
    }
}
