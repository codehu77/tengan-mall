package com.tengan.mall.seckill.application.activity;

import java.util.List;

/** skuIds 是呼叫端已知的「這個商品目前全部的規格」（用來界定要覆蓋的範圍），items 是這個商品要存的
 * 最終結果（可能是子集，甚至是空清單代表整個商品從活動移除），見 ReplaceProductSkusService 說明。 */
public record ReplaceProductSkusCommand(Long activityId, List<Long> skuIds, List<SkuItem> items) {
}
