package com.tengan.mall.payment.application.linepay;

import com.tengan.mall.payment.application.port.OrderPort;
import com.tengan.mall.payment.domain.exception.PaymentGatewayException;
import com.tengan.mall.payment.domain.exception.PaymentRecordNotFoundException;
import com.tengan.mall.payment.domain.exception.PaymentTransactionMismatchException;
import com.tengan.mall.payment.domain.model.PaymentRecord;
import com.tengan.mall.payment.domain.model.PaymentStatus;
import com.tengan.mall.payment.domain.repository.PaymentRecordRepository;
import com.tengan.mall.payment.infrastructure.linepay.LinePayPaymentGatewayClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * 使用者瀏覽器被 LINE Pay 導回 confirmUrl 後，前端呼叫這支——真正扣款發生在這裡（Request API 當下
 * 只是「授權」，不是扣款，見 LINE Pay Online API v3 的兩段式設計）。重複呼叫（使用者重新整理付款頁）
 * 要冪等：已經是 PAID 就直接視為成功，不重複呼叫 LINE Pay Confirm API（避免重複扣款）。
 */
@Service
public class ConfirmLinePayPaymentService implements ConfirmLinePayPaymentUseCase {

    private static final Logger log = LoggerFactory.getLogger(ConfirmLinePayPaymentService.class);

    private final PaymentRecordRepository paymentRecordRepository;
    private final LinePayPaymentGatewayClient linePayPaymentGatewayClient;
    private final OrderPort orderPort;

    public ConfirmLinePayPaymentService(PaymentRecordRepository paymentRecordRepository,
            LinePayPaymentGatewayClient linePayPaymentGatewayClient, OrderPort orderPort) {
        this.paymentRecordRepository = paymentRecordRepository;
        this.linePayPaymentGatewayClient = linePayPaymentGatewayClient;
        this.orderPort = orderPort;
    }

    @Override
    @Transactional
    public void confirm(ConfirmLinePayPaymentCommand command) {
        PaymentRecord record = paymentRecordRepository.findByOrderSn(command.orderSn())
                .orElseThrow(() -> new PaymentRecordNotFoundException(command.orderSn()));

        if (!record.getMemberId().equals(command.memberId())) {
            throw new PaymentTransactionMismatchException(command.orderSn());
        }
        if (record.getStatus() == PaymentStatus.PAID) {
            return;
        }
        if (!command.transactionId().equals(record.getGatewayTradeNo())) {
            throw new PaymentTransactionMismatchException(command.orderSn());
        }

        boolean success = linePayPaymentGatewayClient.confirm(command.transactionId(), record.getAmount());
        if (!success) {
            throw new PaymentGatewayException("LINE Pay 付款確認失敗: orderSn=" + command.orderSn());
        }

        if (!paymentRecordRepository.markPaid(command.orderSn(), command.transactionId())) {
            log.info("LINE Pay confirm 重複呼叫，冪等略過: orderSn={}", command.orderSn());
            return;
        }
        try {
            orderPort.markPaid(command.orderSn());
        } catch (RuntimeException e) {
            log.warn("tengan-order markPaid 失敗（可能與逾時取消競速）: orderSn={}", command.orderSn(), e);
        }
    }
}
