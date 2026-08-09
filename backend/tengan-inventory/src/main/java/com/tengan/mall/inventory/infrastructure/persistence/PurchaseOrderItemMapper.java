package com.tengan.mall.inventory.infrastructure.persistence;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import java.util.List;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

@Mapper
public interface PurchaseOrderItemMapper extends BaseMapper<PurchaseOrderItemPO> {

    @Select("SELECT * FROM purchase_order_item WHERE po_id = #{poId}")
    List<PurchaseOrderItemPO> findByPoId(@Param("poId") Long poId);

    @Update("UPDATE purchase_order_item SET received_qty = #{receivedQty} WHERE id = #{id}")
    int updateReceivedQty(@Param("id") Long id, @Param("receivedQty") int receivedQty);
}
