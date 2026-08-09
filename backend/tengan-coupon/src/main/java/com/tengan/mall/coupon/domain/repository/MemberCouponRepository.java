package com.tengan.mall.coupon.domain.repository;

import com.tengan.mall.coupon.domain.model.MemberCoupon;
import java.util.List;
import java.util.Optional;

public interface MemberCouponRepository {

    MemberCoupon save(MemberCoupon coupon);

    Optional<MemberCoupon> findById(Long id);

    List<MemberCoupon> findByUserId(Long userId);

    /** 條件式 UPDATE use_status=USED WHERE id=? AND use_status=UNUSED，回傳是否真的搶到核銷權。 */
    boolean consume(Long id, String orderSn);

    /** 條件式 UPDATE use_status=UNUSED WHERE id=? AND use_status=USED AND order_sn=?，避免誤退其他訂單用掉的券。 */
    boolean revert(Long id, String orderSn);
}
