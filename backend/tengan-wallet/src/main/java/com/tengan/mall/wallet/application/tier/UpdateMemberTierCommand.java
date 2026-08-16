package com.tengan.mall.wallet.application.tier;

import com.tengan.mall.wallet.domain.model.MemberTierLevel;

public record UpdateMemberTierCommand(Long memberId, MemberTierLevel tier, String reason, String operator) {
}
