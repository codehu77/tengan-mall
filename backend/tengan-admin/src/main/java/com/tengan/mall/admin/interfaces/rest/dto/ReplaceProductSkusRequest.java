package com.tengan.mall.admin.interfaces.rest.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import java.util.List;

/** skuIds 是這個商品目前全部的規格（界定覆蓋範圍）；items 可以是空清單，代表整個商品從活動移除。 */
public record ReplaceProductSkusRequest(@NotNull List<Long> skuIds, @Valid List<SeckillSkuItemRequest> items) {
}
