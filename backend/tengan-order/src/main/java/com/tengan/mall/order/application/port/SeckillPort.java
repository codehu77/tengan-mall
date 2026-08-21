package com.tengan.mall.order.application.port;

import java.util.List;
import java.util.Map;

public interface SeckillPort {

    /**
     * 批次查詢購物車裡哪些 skuId 現在是活躍秒殺（見 Phase 9 規劃第 4.2 節「判斷路徑」）。
     * 回傳的 map 只含真的活躍的 skuId，不在裡面的視為一般商品，走 {@link InventoryPort#lock} 那條路。
     */
    Map<Long, ActiveSeckillSku> checkActive(List<Long> skuIds);

    /**
     * 三道防線本體（限購計數+RSemaphore），失敗時 tengan-seckill 回非 2xx，RestClient 預設行為
     * 直接拋例外——比照 {@link WalletPort#consume} 對「餘額不足」的既有處理方式（不特別包裝翻譯，
     * 讓 CreateOrderService 的補償堆疊 catch(RuntimeException) 接住即可觸發整筆訂單回滾）。
     */
    void reserve(Long skuId, Long memberId, int count);

    /** 補償動作：把已經保留成功的秒殺配額還回去。 */
    void release(Long skuId, Long memberId, int count);
}
