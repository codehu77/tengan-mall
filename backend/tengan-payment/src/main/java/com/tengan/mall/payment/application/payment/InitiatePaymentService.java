package com.tengan.mall.payment.application.payment;

import com.tengan.mall.payment.application.port.OrderDetailForPayment;
import com.tengan.mall.payment.application.port.OrderPort;
import com.tengan.mall.payment.domain.exception.OrderNotPayableException;
import com.tengan.mall.payment.domain.exception.PaymentAlreadyPaidException;
import com.tengan.mall.payment.domain.model.PaymentRecord;
import com.tengan.mall.payment.domain.repository.PaymentRecordRepository;
import com.tengan.mall.payment.infrastructure.ecpay.EcpayPaymentGatewayClient;
import com.tengan.mall.payment.infrastructure.linepay.LinePayPaymentGatewayClient;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.concurrent.ThreadLocalRandom;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * 依 method 分流三種完全不同的金流流程（2026-08-12 修正）：
 * cod 同步完成、credit_card 走 ECPay 表單、linepay 走官方 Request API 換 paymentUrl。
 *
 * <p>cod/linepay 沒有 ECPay MerchantTradeNo 唯一性問題，重試直接刪舊 PENDING 記錄重來
 * （{@link PaymentRecordRepository#deleteIfPending}）。credit_card 不一樣——ECPay 官方規定
 * MerchantTradeNo 為唯一值不可重複使用，使用者按上一頁重新付款不能沿用同一組值，必須先同步查詢
 * ECPay 該筆舊嘗試的真實狀態才能決定要不要作廢重來（見 {@link #initiateCreditCard}）。</p>
 */
@Service
public class InitiatePaymentService implements InitiatePaymentUseCase {

    private static final DateTimeFormatter MERCHANT_TRADE_NO_TIMESTAMP = DateTimeFormatter
            .ofPattern("yyyyMMddHHmmss");

    private final OrderPort orderPort;
    private final PaymentRecordRepository paymentRecordRepository;
    private final EcpayPaymentGatewayClient ecpayPaymentGatewayClient;
    private final LinePayPaymentGatewayClient linePayPaymentGatewayClient;
    private final ReconcilePaymentUseCase reconcilePaymentUseCase;
    private final String clientBackUrl;

    public InitiatePaymentService(OrderPort orderPort, PaymentRecordRepository paymentRecordRepository,
            EcpayPaymentGatewayClient ecpayPaymentGatewayClient, LinePayPaymentGatewayClient linePayPaymentGatewayClient,
            ReconcilePaymentUseCase reconcilePaymentUseCase,
            @Value("${tengan.payment.client-back-url}") String clientBackUrl) {
        this.orderPort = orderPort;
        this.paymentRecordRepository = paymentRecordRepository;
        this.ecpayPaymentGatewayClient = ecpayPaymentGatewayClient;
        this.linePayPaymentGatewayClient = linePayPaymentGatewayClient;
        this.reconcilePaymentUseCase = reconcilePaymentUseCase;
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

        paymentRecordRepository.findPaidByOrderSn(command.orderSn()).ifPresent(existing -> {
            throw new PaymentAlreadyPaidException(command.orderSn());
        });

        return switch (command.method()) {
            case "cod" -> initiateCod(order);
            case "credit_card" -> initiateCreditCard(order);
            case "linepay" -> initiateLinePay(order);
            default -> throw new IllegalArgumentException("不支援的付款方式: " + command.method());
        };
    }

    private InitiatePaymentResult initiateCod(OrderDetailForPayment order) {
        paymentRecordRepository.deleteIfPending(order.orderSn());
        paymentRecordRepository
                .save(PaymentRecord.paid(order.orderSn(), order.memberId(), "cod", order.payAmount()));
        orderPort.markPaid(order.orderSn());
        return new InitiatePaymentResult("cod", null, null);
    }

    /**
     * 若有舊的 PENDING credit_card 嘗試，先同步查 ECPay 真實狀態（{@link ReconcilePaymentUseCase}）：
     * 查到已付款就直接收斂、不建新單（丟 {@link PaymentAlreadyPaidException}，跟「已經付過款」的既有
     * 錯誤語意一致，前端不用另外處理）；查到未付款才生成全新的 MerchantTradeNo 重新發起。殘餘風險
     * （查詢那零點幾秒的競速窗口）已跟使用者確認可接受，不用為此加時間閾值或中介 UI。
     */
    private InitiatePaymentResult initiateCreditCard(OrderDetailForPayment order) {
        var stale = paymentRecordRepository.findLatestPendingCreditCardByOrderSn(order.orderSn());
        if (stale.isPresent()) {
            boolean paid = reconcilePaymentUseCase.reconcile(stale.get());
            if (paid) {
                throw new PaymentAlreadyPaidException(order.orderSn());
            }
        }

        String merchantTradeNo = generateMerchantTradeNo();
        paymentRecordRepository.save(PaymentRecord.pending(order.orderSn(), order.memberId(), "credit_card",
                order.payAmount(), merchantTradeNo, null));
        EcpayFormData form = ecpayPaymentGatewayClient.buildCreditCardForm(merchantTradeNo, order.orderSn(),
                order.payAmount(), order.itemName());
        return new InitiatePaymentResult("credit_card", form, null);
    }

    private InitiatePaymentResult initiateLinePay(OrderDetailForPayment order) {
        paymentRecordRepository.deleteIfPending(order.orderSn());
        String confirmUrl = clientBackUrl + "?orderSn=" + order.orderSn();
        String cancelUrl = clientBackUrl + "?orderSn=" + order.orderSn() + "&cancelled=1";
        LinePayRequestResult result = linePayPaymentGatewayClient.request(order.orderSn(), order.payAmount(),
                order.itemName(), confirmUrl, cancelUrl);
        paymentRecordRepository.save(PaymentRecord.pending(order.orderSn(), order.memberId(), "linepay",
                order.payAmount(), null, result.transactionId()));
        return new InitiatePaymentResult("linepay", null, result.paymentUrlWeb());
    }

    /** PAY + 14 碼時間戳 + 3 碼亂數 = 20 碼，剛好符合 ECPay MerchantTradeNo ≤20 碼限制（仿 SubscribeService 的模式）。 */
    private String generateMerchantTradeNo() {
        String timestamp = MERCHANT_TRADE_NO_TIMESTAMP.format(LocalDateTime.now());
        int random = ThreadLocalRandom.current().nextInt(1000);
        return "PAY" + timestamp + String.format("%03d", random);
    }
}
