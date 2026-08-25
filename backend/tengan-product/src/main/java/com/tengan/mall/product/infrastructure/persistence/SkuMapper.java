package com.tengan.mall.product.infrastructure.persistence;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Update;

@Mapper
public interface SkuMapper extends BaseMapper<SkuPO> {

    /** sale_count 只會遞增（訂單完成觸發），不用像庫存扣減那樣加下限守衛。 */
    @Update("UPDATE sku SET sale_count = sale_count + #{delta} WHERE id = #{skuId}")
    int incrementSaleCount(@Param("skuId") Long skuId, @Param("delta") int delta);
}
