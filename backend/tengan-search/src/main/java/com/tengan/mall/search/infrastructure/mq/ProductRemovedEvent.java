package com.tengan.mall.search.infrastructure.mq;

import java.util.List;

/**
 * 跟 tengan-product 端的事件形狀對齊（cart/seckill 也在消費同一個事件，帶著 skuIds 供它們清理用）——
 * tengan-search 自己只用得到 spuId：索引改成 SPU 層級後，刪除是刪整份文件，不用比對哪些 skuId。
 */
public record ProductRemovedEvent(Long spuId, List<Long> skuIds) {
}
