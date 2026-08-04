package com.tengan.mall.product.application.spu;

import java.util.List;

/**
 * CQRS-lite：後台列表分頁只需要攤平的 {@link SpuSummary} 投影（含 skuCount 這種聚合數字），不需要
 * 載入每一頁的 Spu 聚合根連同底下所有 Sku 明細，所以獨立開一個 Port 直接查 spu 表 + 批次算
 * skuCount，不經過 SpuRepository（跟 SkuDetailPort 是同一個原則，見該 Port 的註解）。
 */
public interface SpuSearchPort {

    List<SpuSummary> search(SpuSearchCriteria criteria, int pageNum, int pageSize);

    long countSearch(SpuSearchCriteria criteria);
}
