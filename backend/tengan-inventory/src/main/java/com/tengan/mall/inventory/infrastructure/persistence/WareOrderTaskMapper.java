package com.tengan.mall.inventory.infrastructure.persistence;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Update;

/** SQL 裡的 1/2/3 直接對應 WareTaskStatus 的 LOCKED/RELEASED/DEDUCTED（@Update 手寫 SQL 不走 IEnum 轉換）。 */
@Mapper
public interface WareOrderTaskMapper extends BaseMapper<WareOrderTaskPO> {

    @Update("UPDATE ware_order_task SET status = 2 WHERE order_sn = #{orderSn} AND status = 1")
    int markReleased(@Param("orderSn") String orderSn);

    @Update("UPDATE ware_order_task SET status = 3 WHERE order_sn = #{orderSn} AND status = 1")
    int markDeducted(@Param("orderSn") String orderSn);
}
