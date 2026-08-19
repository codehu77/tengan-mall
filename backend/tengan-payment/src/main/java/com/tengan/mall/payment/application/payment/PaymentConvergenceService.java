package com.tengan.mall.payment.application.payment;

import com.tengan.mall.payment.application.port.OrderPort;
import com.tengan.mall.payment.domain.model.PaymentRecord;
import com.tengan.mall.payment.domain.repository.PaymentRecordRepository;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

/**
 * 「一筆 payment_record 標記 PAID + 通知 tengan-order」這個收斂動作有兩個觸發來源——ECPay webhook
 * （{@link com.tengan.mall.payment.application.webhook.HandleEcpayCallbackService}）跟使用者重試時的
 * 同步查帳（{@link ReconcilePaymentService}），共用同一支才能確保兩邊的冪等/事後偵測邏輯完全一致。
 */
@Service
public class PaymentConvergenceService {

    private static final Logger log = LoggerFactory.getLogger(PaymentConvergenceService.class);

    private final PaymentRecordRepository paymentRecordRepository;
    private final OrderPort orderPort;

    public PaymentConvergenceService(PaymentRecordRepository paymentRecordRepository, OrderPort orderPort) {
        this.paymentRecordRepository = paymentRecordRepository;
        this.orderPort = orderPort;
    }

    /**
     * 條件式標記 PAID → 呼叫 tengan-order → 事後偵測同一張訂單是否有多筆 PAID（見 Phase 8.6 擴充討論的
     * 殘餘風險：使用者開兩個分頁各自付款成功）。回傳是否真的搶到操作權（false 代表冪等重複，呼叫端不用
     * 再做任何事）。
     */
    public boolean convergeToPaid(PaymentRecord record, String gatewayTradeNo) {
        if (!paymentRecordRepository.markPaid(record.getId(), gatewayTradeNo)) {
            return false;
        }
        try {
            orderPort.markPaid(record.getOrderSn());
        } catch (RuntimeException e) {
            // 已知邊界情況：訂單可能剛好被逾時取消排程搶先轉 CANCELLED，這裡的 payment_record 已經確實
            // 標記為 PAID（畢竟真的收到錢了），不做自動退款，記 WARN 留人工介入。
            log.warn("tengan-order markPaid 失敗（可能與逾時取消競速）: orderSn={}", record.getOrderSn(), e);
        }
        List<PaymentRecord> paidRows = paymentRecordRepository.findAllPaidByOrderSn(record.getOrderSn());
        if (paidRows.size() > 1) {
            // 不是防止雙重扣款，是把「顧客幾週後才發現、來客訴」變成「系統幾秒內自己發現、主動標記給
            // 客服搶在顧客發現前處理」——已知且接受的極小機率殘留風險（兩個分頁各自完成付款）。
            log.error("疑似同一張訂單被重複付款成功，orderSn={} 目前 PAID 記錄數={}，需要人工查帳確認是否要退款",
                    record.getOrderSn(), paidRows.size());
        }
        return true;
    }
}
