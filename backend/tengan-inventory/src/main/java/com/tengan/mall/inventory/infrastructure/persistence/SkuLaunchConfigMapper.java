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

    /**
     * 只覆蓋從 tengan-product 同步的欄位（sale_start_time/purchase_limit_per_user），絕不觸碰
     * traffic_gate_enabled/gate_close_time 跟 gate_protected_stock/gate_warmed_at/gate_settled_at
     * ——這幾欄改由管理員直接呼叫 {@link #configureGate} 設定，商品編輯同步事件不再處理閘門本身。
     */
    @Insert("INSERT INTO sku_launch_config (sku_id, sale_start_time, purchase_limit_per_user) "
            + "VALUES (#{skuId}, #{saleStartTime}, #{purchaseLimitPerUser}) "
            + "ON DUPLICATE KEY UPDATE sale_start_time = VALUES(sale_start_time), "
            + "purchase_limit_per_user = VALUES(purchase_limit_per_user)")
    int upsert(@Param("skuId") Long skuId, @Param("saleStartTime") LocalDateTime saleStartTime,
            @Param("purchaseLimitPerUser") Integer purchaseLimitPerUser);

    /**
     * 管理員在庫存頁面直接設定庫存流量閘門用。正常情況下絕不觸碰 gate_protected_stock/gate_warmed_at/
     * gate_settled_at 這三欄閘門生命週期狀態。唯一例外：如果這顆 sku 已經結算過（gate_settled_at
     * 有值），而且這次傳入的 gate_close_time 比那個結算時間點還晚——這是管理員把閘門關閉時間改到
     * 未來、真的要重開一輪閘門的明確訊號，此時要把三欄一起重置成 NULL，讓這個 sku 重新變成可以被
     * 預熱的狀態。不這樣做的話，gate_warmed_at 留著舊值會讓 isGateActive() 誤判成「閘門仍在保護中」
     * （它只檢查 gate_warmed_at 是否非 null，沒有連帶檢查 gate_settled_at），但 Redis 的 semaphore
     * 其實已經在上一輪結算時被刪掉了——變成商品永遠顯示 0 剩餘、永遠買不到，而且 gate_warmed_at/
     * gate_settled_at 都非 null 會讓這顆 sku 同時被 findReadyToWarmUp/findReadyToSettle 的 WHERE
     * 條件排除，沒有任何排程救得回來。IF 條件刻意用「還沒被這個語句改到之前」的 gate_settled_at
     * 原始值判斷（gate_protected_stock/gate_warmed_at 這兩欄的 SET 都排在 gate_settled_at 自己的
     * SET 之前，MySQL 同一句 UPDATE 裡後面的欄位看得到前面欄位剛寫入的新值，所以順序不能反過來，
     * 不然條件會被自己剛重置的 gate_settled_at=NULL 污染掉）。
     */
    @Update("UPDATE sku_launch_config SET traffic_gate_enabled = #{trafficGateEnabled}, "
            + "gate_close_time = #{gateCloseTime}, "
            + "gate_protected_stock = IF(gate_settled_at IS NOT NULL AND #{gateCloseTime} > gate_settled_at, "
            + "NULL, gate_protected_stock), "
            + "gate_warmed_at = IF(gate_settled_at IS NOT NULL AND #{gateCloseTime} > gate_settled_at, "
            + "NULL, gate_warmed_at), "
            + "gate_settled_at = IF(gate_settled_at IS NOT NULL AND #{gateCloseTime} > gate_settled_at, "
            + "NULL, gate_settled_at) "
            + "WHERE sku_id = #{skuId}")
    int configureGate(@Param("skuId") Long skuId, @Param("trafficGateEnabled") boolean trafficGateEnabled,
            @Param("gateCloseTime") LocalDateTime gateCloseTime);

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
