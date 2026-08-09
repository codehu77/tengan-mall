package com.tengan.mall.coupon.domain.repository;

import com.tengan.mall.coupon.domain.model.CouponTemplate;
import java.util.List;
import java.util.Optional;

public interface CouponTemplateRepository {

    /** 只用於新增——更新規則/下架走 updateRule/delist，避免整包覆蓋動到 issuedCount（見下方 javadoc）。 */
    CouponTemplate save(CouponTemplate template);

    /** 只動規則欄位，不碰 issued_count/status，避免蓋掉 tryIncrementIssuedCount 剛好同時搶到的核發計數。 */
    void updateRule(CouponTemplate template);

    void delist(Long id);

    Optional<CouponTemplate> findById(Long id);

    List<CouponTemplate> findByIds(List<Long> ids);

    List<CouponTemplate> findAll();

    /**
     * 條件式 UPDATE issued_count=issued_count+1 WHERE id=? AND issued_count<total_count——核發是
     * 併發熱點，不能先讀 issuedCount 再算 +1 存回去，中間有時間窗口會超發，回傳是否搶到額度。
     */
    boolean tryIncrementIssuedCount(Long templateId);
}
