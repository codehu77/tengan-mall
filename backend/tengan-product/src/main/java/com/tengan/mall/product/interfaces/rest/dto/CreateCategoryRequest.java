package com.tengan.mall.product.interfaces.rest.dto;

import jakarta.validation.constraints.NotBlank;

/** parentId 故意不加 @NotNull——null 代表建立頂層分類，跟 CreateCategoryService.resolveLevel() 的既有規則一致（null 或 0 都視為頂層）。 */
public record CreateCategoryRequest(Long parentId, @NotBlank String name, String icon, int sort) {
}
