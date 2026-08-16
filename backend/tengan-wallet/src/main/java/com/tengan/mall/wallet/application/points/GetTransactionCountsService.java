package com.tengan.mall.wallet.application.points;

import com.tengan.mall.wallet.domain.model.PointsTransactionStatus;
import com.tengan.mall.wallet.domain.model.PointsTransactionType;
import com.tengan.mall.wallet.domain.repository.PointsTransactionRepository;
import java.util.List;
import org.springframework.stereotype.Service;

/**
 * 回傳每個 (type, status) 組合各自的筆數，不在這裡假設「待入帳/已生效/已使用/已過期/人工調整/已撤銷」
 * 這種具名分類——分類定義只活在前端 TransactionFilterBar.vue 一個地方，這裡只給原始分組數字。
 */
@Service
public class GetTransactionCountsService implements GetTransactionCountsUseCase {

    private final PointsTransactionRepository pointsTransactionRepository;

    public GetTransactionCountsService(PointsTransactionRepository pointsTransactionRepository) {
        this.pointsTransactionRepository = pointsTransactionRepository;
    }

    @Override
    public List<TransactionCountGroup> get(Long memberId) {
        return pointsTransactionRepository.countGroupedByTypeAndStatus(memberId).stream()
                .map(row -> new TransactionCountGroup(PointsTransactionType.fromCode(row.type()).name(),
                        PointsTransactionStatus.fromCode(row.status()).name(), row.count()))
                .toList();
    }
}
