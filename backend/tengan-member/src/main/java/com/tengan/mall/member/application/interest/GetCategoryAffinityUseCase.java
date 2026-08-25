package com.tengan.mall.member.application.interest;

import java.util.List;

public interface GetCategoryAffinityUseCase {

    List<CategoryAffinityItem> get(Long memberId);
}
