package com.tengan.mall.inventory.infrastructure.persistence;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import java.time.LocalDateTime;
import java.util.List;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

@Mapper
public interface SkuLaunchConfigMapper extends BaseMapper<SkuLaunchConfigPO> {

    /** 只覆蓋從 tengan-product 同步的欄位，絕不觸碰 gate_protected_stock/gate_warmed_at/gate_settled_at。 */
    @Insert("INSERT INTO sku_launch_config (sku_id, sale_start_time, traffic_gate_enabled, gate_close_time, "
            + "purchase_limit_per_user) VALUES (#{skuId}, #{saleStartTime}, #{trafficGateEnabled}, "
            + "#{gateCloseTime}, #{purchaseLimitPerUser}) "
            + "ON DUPLICATE KEY UPDATE sale_start_time = VALUES(sale_start_time), "
            + "traffic_gate_enabled = VALUES(traffic_gate_enabled), gate_close_time = VALUES(gate_close_time), "
            + "purchase_limit_per_user = VALUES(purchase_limit_per_user)")
    int upsert(@Param("skuId") Long skuId, @Param("saleStartTime") LocalDateTime saleStartTime,
            @Param("trafficGateEnabled") boolean trafficGateEnabled,
            @Param("gateCloseTime") LocalDateTime gateCloseTime,
            @Param("purchaseLimitPerUser") Integer purchaseLimitPerUser);

    /**
     * 只用 horizon 當上界（別預熱太早），刻意不拿 now 當 sale_start_time 的下界——如果拿 now 當下界，
     * 一旦排程/手動預熱都沒趕上、sale_start_time 已經過了，這筆就會永遠被這條 WHERE 排除在外、
     * 再也不會被預熱到，等於閘門形同虛設卻沒人知道。改用 gate_close_time > now 當「這個閘門還有沒
     * 有意義」的唯一時間門檻：只要閘門還沒真正關閉，即使開賣時間已經過了也該立刻補預熱，讓已經逾期
     * 的商品照樣能透過「立即預熱」或下一輪排程救回來，而不是放著永遠停在未預熱狀態。
     */
    @Select("SELECT * FROM sku_launch_config WHERE traffic_gate_enabled = 1 AND gate_warmed_at IS NULL "
            + "AND sale_start_time IS NOT NULL AND sale_start_time <= #{horizon} "
            + "AND gate_close_time IS NOT NULL AND gate_close_time > #{now}")
    List<SkuLaunchConfigPO> findReadyToWarmUp(@Param("now") LocalDateTime now, @Param("horizon") LocalDateTime horizon);

    @Update("UPDATE sku_launch_config SET gate_protected_stock = #{protectedStock}, gate_warmed_at = #{warmedAt} "
            + "WHERE sku_id = #{skuId} AND gate_warmed_at IS NULL")
    int markWarmed(@Param("skuId") Long skuId, @Param("protectedStock") int protectedStock,
            @Param("warmedAt") LocalDateTime warmedAt);

    @Select("SELECT * FROM sku_launch_config WHERE traffic_gate_enabled = 1 AND gate_warmed_at IS NOT NULL "
            + "AND gate_settled_at IS NULL AND gate_close_time IS NOT NULL AND gate_close_time <= #{now}")
    List<SkuLaunchConfigPO> findReadyToSettle(@Param("now") LocalDateTime now);

    @Update("UPDATE sku_launch_config SET gate_settled_at = #{settledAt} "
            + "WHERE sku_id = #{skuId} AND gate_settled_at IS NULL")
    int markSettled(@Param("skuId") Long skuId, @Param("settledAt") LocalDateTime settledAt);

    @Select("SELECT * FROM sku_launch_config WHERE traffic_gate_enabled = 1 ORDER BY sku_id DESC")
    List<SkuLaunchConfigPO> findAllGateEnabled();

    @Delete("<script>DELETE FROM sku_launch_config WHERE sku_id IN "
            + "<foreach collection='skuIds' item='skuId' open='(' separator=',' close=')'>#{skuId}</foreach>"
            + "</script>")
    int deleteBySkuIds(@Param("skuIds") List<Long> skuIds);
}
