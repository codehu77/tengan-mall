package com.tengan.mall.member.application.interest;

import com.tengan.mall.member.domain.model.CategoryInterestSource;

public record RecordCategoryInterestCommand(Long memberId, Long categoryId, CategoryInterestSource source) {
}
