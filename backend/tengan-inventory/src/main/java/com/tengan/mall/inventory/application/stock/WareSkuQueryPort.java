package com.tengan.mall.inventory.application.stock;

import java.util.List;

/**
 * CQRS-lite 唯讀查詢，不透過 WareSkuRepository（那個介面只暴露條件式 SQL 意圖方法，見其 javadoc）。
 * keyword 目前只對 sku_id 做前綴比對（inventory 服務不知道商品名稱，名稱顯示由 tengan-admin
 * 批次查 tengan-product 補齊，這是文件本身沒有寫死細節、刻意的簡化，見開發規劃記錄）。
 */
public interface WareSkuQueryPort {

    List<WareSkuQueryItem> search(Long wareId, Long skuIdKeyword, int pageNum, int pageSize);

    long countSearch(Long wareId, Long skuIdKeyword);
}
