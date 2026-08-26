package com.tengan.mall.inventory.domain.repository;

import com.tengan.mall.inventory.domain.model.SkuLaunchConfig;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface SkuLaunchConfigRepository {

    Optional<SkuLaunchConfig> findBySkuId(Long skuId);

    /**
     * 訂閱 tengan-product 的 product.launch-config.upserted 事件用，skuId 是全域唯一 PK，直接覆蓋
     * 既有值——只覆蓋這幾個從 product 同步的欄位，絕對不動 gate_protected_stock/gate_warmed_at/
     * gate_settled_at 這三欄 inventory 自己的閘門生命週期狀態。
     */
    void upsert(Long skuId, LocalDateTime saleStartTime, boolean trafficGateEnabled, LocalDateTime gateCloseTime,
            Integer purchaseLimitPerUser);

    /** warm-up 排程用：找開了閘門、還沒預熱、開賣時間落在 [now, horizon] 內的候選。 */
    List<SkuLaunchConfig> findReadyToWarmUp(LocalDateTime now, LocalDateTime horizon);

    /** warm-up 成功後標記，條件式 UPDATE(WHERE gate_warmed_at IS NULL)避免重複預熱，回傳是否真的搶到。 */
    boolean markWarmed(Long skuId, int protectedStock, LocalDateTime warmedAt);

    /** 結算排程用：找已預熱、還沒結算、閘門關閉時間已過的候選。 */
    List<SkuLaunchConfig> findReadyToSettle(LocalDateTime now);

    /** 結算成功後標記，條件式 UPDATE(WHERE gate_settled_at IS NULL)避免重複結算，回傳是否真的搶到。 */
    boolean markSettled(Long skuId, LocalDateTime settledAt);

    /** 後台「流量閘門」頁面用：列出所有開啟閘門的 sku（不管有沒有預熱/結算過）。 */
    List<SkuLaunchConfig> findAllGateEnabled();

    /** 訂閱 product.launch-config.removed 事件用：SPU 整批替換 SKU 時，舊 skuId 的副本列要跟著清掉，
     * 不然會一直累積查不到對應商品的孤兒列。 */
    void deleteBySkuIds(List<Long> skuIds);
}
