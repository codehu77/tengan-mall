package com.tengan.mall.product.application.spu;

import java.util.List;

/**
 * CQRS-lite：首頁/看更多頁只需要攤平的 spuId/name/mainImage/代表價格/開賣時間，不需要載入完整聚合根，
 * 獨立開一個 Port 直接查 spu 表 + 批次抓代表 SKU 價格，不經過 SpuRepository（同 SpuSearchPort 的原則）。
 * 查詢條件固定：status=ON_SHELF AND show_on_launch_teaser=true AND (teaser_remove_at IS NULL OR
 * teaser_remove_at > now)。
 */
public interface LaunchTeaserQueryPort {

    List<LaunchTeaserSpuView> listForHome(int limit);

    List<LaunchTeaserSpuView> search(int pageNum, int pageSize);

    long count();
}
