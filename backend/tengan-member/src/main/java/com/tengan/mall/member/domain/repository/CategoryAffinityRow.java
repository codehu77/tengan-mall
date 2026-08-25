package com.tengan.mall.member.domain.repository;

/** 某分類的興趣分數（已經套用權重、時間窗口計算完畢），依分數高到低排序回傳。 */
public record CategoryAffinityRow(Long categoryId, int score) {
}
