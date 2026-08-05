package com.tengan.mall.product.application.spu;

/**
 * 供 tengan-search 重建索引拉取用——分頁單位是「spu」，不是「sku」（一個 spu 底下有多顆 sku，
 * 回傳的 sku 筆數會浮動），呼叫端只需要知道 hasNext 就能決定要不要翻下一頁。
 */
public interface SearchExportSpusUseCase {

    SearchExportSpusResult export(int pageNum, int pageSize);
}
