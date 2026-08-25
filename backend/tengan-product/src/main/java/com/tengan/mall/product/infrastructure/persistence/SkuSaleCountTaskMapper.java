package com.tengan.mall.product.infrastructure.persistence;

import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

/** order.completed 消費冪等去重表——只寫入、從不查詢，不用 BaseMapper/PO。 */
@Mapper
public interface SkuSaleCountTaskMapper {

    /** 回傳 1=第一次處理這個 orderSn（搶到操作權）；0=UNIQUE KEY 擋下重複投遞，no-op。 */
    @Insert("INSERT IGNORE INTO sku_sale_count_task (order_sn) VALUES (#{orderSn})")
    int tryClaim(@Param("orderSn") String orderSn);
}
