package com.tengan.mall.inventory.domain.exception;

/**
 * 啟用庫存流量閘門前這顆 SKU 必須已經有真實庫存，不然 warm-up 排程會把 0 快照成
 * gate_protected_stock、gate_warmed_at 寫入非 null 後永久排除在 findReadyToWarmUp 之外，
 * 之後才補的庫存永遠不會被重新快照——這是這次把閘門設定搬來庫存頁面要從源頭擋住的問題。
 */
public class NoStockForGateException extends RuntimeException {

    public NoStockForGateException(Long skuId) {
        super("請先建立這顆 SKU 的庫存再啟用庫存流量閘門: skuId=" + skuId);
    }
}
