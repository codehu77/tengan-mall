package com.tengan.mall.inventory.infrastructure.mq;

/** 跟 tengan-order 發布端的事件形狀（只有 orderSn 一個欄位）用 JSON 欄位名稱對齊，不共用型別。 */
public record OrderSnEvent(String orderSn) {
}
