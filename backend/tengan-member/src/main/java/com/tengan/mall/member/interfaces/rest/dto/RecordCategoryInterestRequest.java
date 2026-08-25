package com.tengan.mall.member.interfaces.rest.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

/** source 傳可讀字串 "VIEW"/"SEARCH"（比照 paymentMethod 用字串不用數字的既有慣例），controller 轉 enum。 */
public record RecordCategoryInterestRequest(@NotNull Long categoryId, @NotBlank String source) {
}
