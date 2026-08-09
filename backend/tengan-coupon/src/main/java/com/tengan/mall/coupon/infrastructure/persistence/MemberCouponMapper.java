package com.tengan.mall.coupon.infrastructure.persistence;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Update;

@Mapper
public interface MemberCouponMapper extends BaseMapper<MemberCouponPO> {

    @Update("UPDATE member_coupon SET use_status = 2, order_sn = #{orderSn} WHERE id = #{id} AND use_status = 1")
    int consume(@Param("id") Long id, @Param("orderSn") String orderSn);

    @Update("UPDATE member_coupon SET use_status = 1, order_sn = NULL "
            + "WHERE id = #{id} AND use_status = 2 AND order_sn = #{orderSn}")
    int revert(@Param("id") Long id, @Param("orderSn") String orderSn);
}
