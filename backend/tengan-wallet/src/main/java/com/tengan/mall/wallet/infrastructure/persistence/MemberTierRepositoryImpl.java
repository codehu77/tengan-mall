package com.tengan.mall.wallet.infrastructure.persistence;

import com.tengan.mall.wallet.domain.model.MemberTier;
import com.tengan.mall.wallet.domain.model.MemberTierLevel;
import com.tengan.mall.wallet.domain.repository.MemberTierRepository;
import java.time.ZoneId;
import java.util.Optional;
import org.springframework.stereotype.Repository;

@Repository
public class MemberTierRepositoryImpl implements MemberTierRepository {

    private final MemberTierMapper mapper;

    public MemberTierRepositoryImpl(MemberTierMapper mapper) {
        this.mapper = mapper;
    }

    @Override
    public Optional<MemberTier> findByMemberId(Long memberId) {
        return Optional.ofNullable(mapper.selectById(memberId))
                .map(po -> MemberTier.reconstitute(po.getMemberId(), po.getTier(), po.getUpdatedBy(),
                        po.getUpdatedReason(),
                        po.getUpdatedAt() == null ? null : po.getUpdatedAt().atZone(ZoneId.systemDefault()).toInstant()));
    }

    @Override
    public void upsert(Long memberId, MemberTierLevel tier, String updatedBy, String updatedReason) {
        mapper.upsert(memberId, tier.getValue(), updatedBy, updatedReason);
    }
}
