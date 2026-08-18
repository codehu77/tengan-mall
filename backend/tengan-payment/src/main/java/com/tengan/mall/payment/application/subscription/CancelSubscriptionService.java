package com.tengan.mall.payment.application.subscription;

import com.tengan.mall.payment.domain.exception.SubscriptionNotFoundException;
import com.tengan.mall.payment.domain.repository.SubscriptionRepository;
import org.springframework.stereotype.Service;

/**
 * 只停用未來續訂，**不**立刻降級——使用者已經付過的那一期還沒到期，權益要維持到
 * {@link com.tengan.mall.payment.domain.model.Subscription#getPaidUntil()} 才由
 * {@link SubscriptionExpiryScheduler} 排程降級（見規劃文件核心設計決策）。
 *
 * <p>已知限制：ECPay 的「信用卡定期定額訂單作業」API（{@code Action=Cancel}）用 AES-GCM 加密，
 * 跟全站其他 ECPay 端點統一使用的 {@code CheckMacValue} 雜湊比對是不同機制，這次規劃時查證
 * 官方文件拿到的範例資料無法驗證通過（base64 解碼後長度對不起來，判斷是文件擷取轉錄時失真），
 * 沒有可靠測試向量前不寫這段加密邏輯——這裡目前只停用系統內部的續訂記錄，**沒有真的呼叫 ECPay
 * 停止扣款**。之後要接上真正的 Cancel Action，需要先用官方 SDK 或另外的管道拿到一組驗證過的
 * AES-GCM 測試向量。</p>
 */
@Service
public class CancelSubscriptionService implements CancelSubscriptionUseCase {

    private final SubscriptionRepository subscriptionRepository;

    public CancelSubscriptionService(SubscriptionRepository subscriptionRepository) {
        this.subscriptionRepository = subscriptionRepository;
    }

    @Override
    public void cancel(CancelSubscriptionCommand command) {
        var subscription = subscriptionRepository.findActiveByMemberId(command.memberId())
                .orElseThrow(() -> new SubscriptionNotFoundException(command.memberId()));
        subscriptionRepository.markCancelled(subscription.getId());
    }
}
