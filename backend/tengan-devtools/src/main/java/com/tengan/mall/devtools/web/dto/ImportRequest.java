package com.tengan.mall.devtools.web.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import java.util.List;

/** 使用者在畫面上編輯確認後送出的最終資料。分類/品牌/規格屬性都是使用者自己在下拉選單選的既有 ID，
 * 這支工具完全不會自動建立分類/品牌/屬性。 */
public record ImportRequest(@NotNull Long categoryId, @NotNull Long brandId, @NotBlank String name,
        String featureText, @NotEmpty List<String> spuImageUrls, List<String> featureImageUrls,
        @Valid List<AttrValueInput> baseAttrValues, @Valid @NotEmpty List<SkuInput> skus) {
}
