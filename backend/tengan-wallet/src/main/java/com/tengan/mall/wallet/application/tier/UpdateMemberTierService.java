package com.tengan.mall.wallet.application.tier;

import com.tengan.mall.wallet.domain.repository.MemberTierRepository;
import org.springframework.stereotype.Service;

/**
 * Phase 8.5（訂閱）上線前唯一的升等入口——比照點數 ADJUST「客服補償」的精神，供 tengan-admin
 * 行銷管理頁呼叫（需 X-Identity-Assertion，operator 記錄哪個客服帳號調整的）。
 */
@Service
public class UpdateMemberTierService implements UpdateMemberTierUseCase {

    private final MemberTierRepository memberTierRepository;

    public UpdateMemberTierService(MemberTierRepository memberTierRepository) {
        this.memberTierRepository = memberTierRepository;
    }

    @Override
    public void update(UpdateMemberTierCommand command) {
        memberTierRepository.upsert(command.memberId(), command.tier(), command.operator(), command.reason());
    }
}
