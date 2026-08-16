package com.tengan.mall.payment.application.payment;

import com.tengan.mall.payment.application.port.OrderDetailForPayment;
import com.tengan.mall.payment.application.port.OrderPort;
import com.tengan.mall.payment.domain.exception.OrderNotPayableException;
import com.tengan.mall.payment.domain.exception.PaymentAlreadyPaidException;
import com.tengan.mall.payment.domain.model.PaymentRecord;
import com.tengan.mall.payment.domain.model.PaymentStatus;
import com.tengan.mall.payment.domain.repository.PaymentRecordRepository;
import com.tengan.mall.payment.infrastructure.ecpay.EcpayPaymentGatewayClient;
import com.tengan.mall.payment.infrastructure.linepay.LinePayPaymentGatewayClient;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * 依 method 分流三種完全不同的金流流程（見 [[payment_gateway_decision]] 2026-08-12 修正）：
 * cod 同步完成、credit_card 走 ECPay 表單、linepay 走官方 Request API 換 paymentUrl。
 *
 * <p>發起付款前先清掉舊的 PENDING 記錄（{@link PaymentRecordRepository#deleteIfPending}），讓使用者
 * 可以在付款頁換一種付款方式重新嘗試——PAID 記錄受 unique index 保護，這支方法只刪未完成的舊嘗試。</p>
 */
@Service
public class InitiatePaymentService implements InitiatePaymentUseCase {

    private final OrderPort orderPort;
    private final PaymentRecordRepository paymentRecordRepository;
    private final EcpayPaymentGatewayClient ecpayPaymentGatewayClient;
    private final LinePayPaymentGatewayClient linePayPaymentGatewayClient;
    private final String clientBackUrl;

    public InitiatePaymentService(OrderPort orderPort, PaymentRecordRepository paymentRecordRepository,
            EcpayPaymentGatewayClient ecpayPaymentGatewayClient, LinePayPaymentGatewayClient linePayPaymentGatewayClient,
            @Value("${tengan.payment.client-back-url}") String clientBackUrl) {
        this.orderPort = orderPort;
        this.paymentRecordRepository = paymentRecordRepository;
        this.ecpayPaymentGatewayClient = ecpayPaymentGatewayClient;
        this.linePayPaymentGatewayClient = linePayPaymentGatewayClient;
        this.clientBackUrl = clientBackUrl;
    }

    @Override
    @Transactional
    public InitiatePaymentResult initiate(InitiatePaymentCommand command) {
        OrderDetailForPayment order = orderPort.getOrder(command.orderSn());
        if (!order.memberId().equals(command.memberId())
                || order.status() != OrderDetailForPayment.STATUS_PENDING_PAYMENT) {
            throw new OrderNotPayableException(command.orderSn());
        }

        paymentRecordRepository.findByOrderSn(command.orderSn()).ifPresent(existing -> {
            if (existing.getStatus() == PaymentStatus.PAID) {
                throw new PaymentAlreadyPaidException(command.orderSn());
            }
        });
        paymentRecordRepository.deleteIfPending(command.orderSn());

        return switch (command.method()) {
            case "cod" -> initiateCod(order);
            case "credit_card" -> initiateCreditCard(order);
            case "linepay" -> initiateLinePay(order);
            default -> throw new IllegalArgumentException("不支援的付款方式: " + command.method());
        };
    }

    private InitiatePaymentResult initiateCod(OrderDetailForPayment order) {
        paymentRecordRepository
                .save(PaymentRecord.paid(order.orderSn(), order.memberId(), "cod", order.payAmount()));
        orderPort.markPaid(order.orderSn());
        return new InitiatePaymentResult("cod", null, null);
    }

    private InitiatePaymentResult initiateCreditCard(OrderDetailForPayment order) {
        paymentRecordRepository.save(
                PaymentRecord.pending(order.orderSn(), order.memberId(), "credit_card", order.payAmount(), null));
        EcpayFormData form = ecpayPaymentGatewayClient.buildCreditCardForm(order.orderSn(), order.payAmount(),
                order.itemName());
        return new InitiatePaymentResult("credit_card", form, null);
    }

    private InitiatePaymentResult initiateLinePay(OrderDetailForPayment order) {
        String confirmUrl = clientBackUrl + "?orderSn=" + order.orderSn();
        String cancelUrl = clientBackUrl + "?orderSn=" + order.orderSn() + "&cancelled=1";
        LinePayRequestResult result = linePayPaymentGatewayClient.request(order.orderSn(), order.payAmount(),
                order.itemName(), confirmUrl, cancelUrl);
        paymentRecordRepository.save(PaymentRecord.pending(order.orderSn(), order.memberId(), "linepay",
                order.payAmount(), result.transactionId()));
        return new InitiatePaymentResult("linepay", null, result.paymentUrlWeb());
    }
}
