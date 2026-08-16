package com.tengan.mall.wallet.domain.repository;

import com.tengan.mall.wallet.domain.model.MemberTier;
import com.tengan.mall.wallet.domain.model.MemberTierLevel;
import java.util.Optional;

public interface MemberTierRepository {

    /** 查無資料回傳 empty，呼叫端一律視為 FREE（見 MemberTier#defaultFree）。 */
    Optional<MemberTier> findByMemberId(Long memberId);

    /** upsert——第一次寫入直接 INSERT，之後改用 UPDATE，呼叫端不需要知道這個會員之前有沒有列。 */
    void upsert(Long memberId, MemberTierLevel tier, String updatedBy, String updatedReason);
}
