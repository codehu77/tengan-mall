package com.tengan.mall.seckill.application.reservation;

import java.util.List;

/**
 * 供 tengan-order 的訂單建立 Saga 判斷購物車裡每個 skuId 該走秒殺保留還是一般庫存鎖定
 * （見規劃第 4.2 節「判斷路徑」）。只回傳目前真的活躍的 SKU，不在回傳清單裡的視為一般商品。
 */
public interface CheckActiveSkusUseCase {

    List<ActiveSkuStatus> check(List<Long> skuIds);
}
