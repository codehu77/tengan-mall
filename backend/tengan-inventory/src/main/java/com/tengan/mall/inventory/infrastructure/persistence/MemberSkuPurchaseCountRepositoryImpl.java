package com.tengan.mall.inventory.infrastructure.persistence;

import com.tengan.mall.inventory.domain.repository.MemberSkuPurchaseCountRepository;
import org.springframework.stereotype.Repository;

@Repository
public class MemberSkuPurchaseCountRepositoryImpl implements MemberSkuPurchaseCountRepository {

    private final MemberSkuPurchaseCountMapper mapper;

    public MemberSkuPurchaseCountRepositoryImpl(MemberSkuPurchaseCountMapper mapper) {
        this.mapper = mapper;
    }

    @Override
    public boolean tryIncrement(Long memberId, Long skuId, int count, int limit) {
        if (mapper.tryIncrement(memberId, skuId, count, limit) > 0) {
            return true;
        }
        // 條件式 UPDATE 沒動到列：可能是還沒有這個 (memberId, skuId) 的列，也可能是已經超過上限。
        // count 本身沒超過 limit 時才值得嘗試 INSERT；INSERT IGNORE 遇到併發搶插入時安靜回傳 0 列，
        // 不用額外 catch DuplicateKeyException。
        boolean inserted = count <= limit && mapper.insertIgnore(memberId, skuId, count) > 0;
        if (inserted) {
            return true;
        }
        // 走到這裡代表列已存在（原本就有，或剛才被併發的另一個請求插入），用同一個條件式 UPDATE 再試一次。
        return mapper.tryIncrement(memberId, skuId, count, limit) > 0;
    }

    @Override
    public void decrement(Long memberId, Long skuId, int count) {
        mapper.decrement(memberId, skuId, count);
    }
}
