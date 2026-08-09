package com.tengan.mall.inventory.infrastructure.persistence;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import java.util.List;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

/**
 * lock/release/deduct 都是條件式 UPDATE（WHERE 帶庫存足量判斷），受影響列數決定成功與否，
 * 不透過「先 selectById 再 updateById」——中間有時間窗口會超賣，見 WareSkuRepository 的 javadoc。
 */
@Mapper
public interface WareSkuMapper extends BaseMapper<WareSkuPO> {

    @Select("SELECT COALESCE(SUM(stock - locked_stock), 0) FROM ware_sku WHERE sku_id = #{skuId}")
    int sumAvailableStock(@Param("skuId") Long skuId);

    @Select("SELECT ware_id FROM ware_sku WHERE sku_id = #{skuId} ORDER BY ware_id")
    List<Long> findCandidateWareIds(@Param("skuId") Long skuId);

    @Update("UPDATE ware_sku SET locked_stock = locked_stock + #{count} "
            + "WHERE ware_id = #{wareId} AND sku_id = #{skuId} AND stock - locked_stock >= #{count}")
    int tryLock(@Param("wareId") Long wareId, @Param("skuId") Long skuId, @Param("count") int count);

    @Update("UPDATE ware_sku SET locked_stock = locked_stock - #{count} "
            + "WHERE ware_id = #{wareId} AND sku_id = #{skuId} AND locked_stock >= #{count}")
    int release(@Param("wareId") Long wareId, @Param("skuId") Long skuId, @Param("count") int count);

    @Update("UPDATE ware_sku SET stock = stock - #{count}, locked_stock = locked_stock - #{count} "
            + "WHERE ware_id = #{wareId} AND sku_id = #{skuId} AND locked_stock >= #{count}")
    int deduct(@Param("wareId") Long wareId, @Param("skuId") Long skuId, @Param("count") int count);

    @Update("UPDATE ware_sku SET stock = stock - #{count} "
            + "WHERE ware_id = #{wareId} AND sku_id = #{skuId} AND stock >= #{count}")
    int deductStockOnly(@Param("wareId") Long wareId, @Param("skuId") Long skuId, @Param("count") int count);

    @Select("SELECT COUNT(*) FROM ware_sku WHERE ware_id = #{wareId} AND sku_id = #{skuId}")
    int countForWareSku(@Param("wareId") Long wareId, @Param("skuId") Long skuId);

    @Insert("INSERT INTO ware_sku (ware_id, sku_id, stock, locked_stock) VALUES (#{wareId}, #{skuId}, #{initialStock}, 0)")
    int insertInitialStock(@Param("wareId") Long wareId, @Param("skuId") Long skuId,
            @Param("initialStock") int initialStock);

    @Update("UPDATE ware_sku SET stock = stock + #{delta} "
            + "WHERE ware_id = #{wareId} AND sku_id = #{skuId} AND stock + #{delta} >= 0")
    int adjustStockDelta(@Param("wareId") Long wareId, @Param("skuId") Long skuId, @Param("delta") int delta);
}
