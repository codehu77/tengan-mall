package com.tengan.mall.inventory.infrastructure.persistence;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Update;

/** SQL 裡的 1/2 直接對應 PurchaseOrderStatus 的 PENDING/RECEIVED（@Update 手寫 SQL 不走 IEnum 轉換）。 */
@Mapper
public interface PurchaseOrderMapper extends BaseMapper<PurchaseOrderPO> {

    @Update("UPDATE purchase_order SET status = 2, received_at = NOW() WHERE id = #{id} AND status = 1")
    int markReceived(@Param("id") Long id);
}
