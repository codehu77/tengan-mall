package com.tengan.mall.inventory.infrastructure.persistence;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Update;

/**
 * tryIncrement/decrement 都是條件式 UPDATE，受影響列數決定成功與否，不透過 select-then-update，
 * 比照 WareSkuMapper 同樣的原子性精神。
 */
@Mapper
public interface MemberSkuPurchaseCountMapper extends BaseMapper<MemberSkuPurchaseCountPO> {

    @Update("UPDATE member_sku_purchase_count SET purchased_count = purchased_count + #{count} "
            + "WHERE member_id = #{memberId} AND sku_id = #{skuId} AND purchased_count + #{count} <= #{limit}")
    int tryIncrement(@Param("memberId") Long memberId, @Param("skuId") Long skuId, @Param("count") int count,
            @Param("limit") int limit);

    @Insert("INSERT IGNORE INTO member_sku_purchase_count (member_id, sku_id, purchased_count) "
            + "VALUES (#{memberId}, #{skuId}, #{count})")
    int insertIgnore(@Param("memberId") Long memberId, @Param("skuId") Long skuId, @Param("count") int count);

    @Update("UPDATE member_sku_purchase_count SET purchased_count = purchased_count - #{count} "
            + "WHERE member_id = #{memberId} AND sku_id = #{skuId} AND purchased_count >= #{count}")
    int decrement(@Param("memberId") Long memberId, @Param("skuId") Long skuId, @Param("count") int count);
}
