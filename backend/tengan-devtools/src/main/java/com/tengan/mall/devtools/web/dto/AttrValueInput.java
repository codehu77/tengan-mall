package com.tengan.mall.devtools.web.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

/** standardValueId 選填——操作者若知道這個屬性值該對應哪個標準聚合值可以直接帶入，
 * 不知道就留空，匯入後仍以「未綁定」狀態正常運作（見規格屬性單位+標準聚合值正規化功能）。 */
public record AttrValueInput(@NotNull Long attrId, @NotBlank String value, Long standardValueId) {
}
