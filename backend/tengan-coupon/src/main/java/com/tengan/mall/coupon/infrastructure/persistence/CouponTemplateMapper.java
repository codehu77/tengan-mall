package com.tengan.mall.coupon.infrastructure.persistence;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Update;

@Mapper
public interface CouponTemplateMapper extends BaseMapper<CouponTemplatePO> {

    @Update("UPDATE coupon_template SET issued_count = issued_count + 1 "
            + "WHERE id = #{templateId} AND issued_count < total_count")
    int tryIncrementIssuedCount(@Param("templateId") Long templateId);

    /**
     * 只動規則欄位、不碰 issued_count/status——避免 updateById 用整包 PO 覆蓋時，把 tryIncrementIssuedCount
     * 剛好同時搶到的核發計數蓋回舊值（updateRule 是低頻管理操作，但沒必要留這個時間窗口）。
     */
    @Update("UPDATE coupon_template SET name=#{po.name}, threshold_amount=#{po.thresholdAmount}, "
            + "discount_amount=#{po.discountAmount}, total_count=#{po.totalCount}, "
            + "effective_start=#{po.effectiveStart}, effective_end=#{po.effectiveEnd} WHERE id=#{po.id}")
    int updateRuleFields(@Param("po") CouponTemplatePO po);

    @Update("UPDATE coupon_template SET status = 2 WHERE id = #{id}")
    int delist(@Param("id") Long id);
}
