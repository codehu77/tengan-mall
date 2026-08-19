package com.tengan.mall.payment.application.webhook;

import com.tengan.mall.payment.application.payment.PaymentConvergenceService;
import com.tengan.mall.payment.domain.exception.InvalidCheckMacValueException;
import com.tengan.mall.payment.domain.model.PaymentRecord;
import com.tengan.mall.payment.domain.repository.PaymentRecordRepository;
import com.tengan.mall.payment.infrastructure.ecpay.EcpayPaymentGatewayClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * ECPay 一般訂單的 ReturnURL 通知——這是「訂單已付款」這個事實的其中一個觸發點（另一個是
 * LINE Pay confirm、還有 COD 同步）。驗簽 → 用 callback 帶的 MerchantTradeNo 反查 payment_record
 * （這個欄位是每次付款嘗試各自生成的，不是 orderSn，見 {@link PaymentRecord#getEcpayMerchantTradeNo()}）
 * → 條件式 UPDATE 搶操作權 → 呼叫 tengan-order。搶不到操作權代表是重複通知（ECPay 官方會重試），
 * 直接視為成功、不重複呼叫 tengan-order（冪等）。
 *
 * <p>訂閱單走的是完全獨立的 {@code ecpay-subscription-return-url}（見
 * {@link HandleSubscriptionReturnCallbackService}），不共用這支——兩者是不同的 use case，訂閱單本來
 * 就不會有 payment_record，硬塞進同一支只會讓「查不到訂單」這個分支背負兩種不同語意。</p>
 */
@Service
public class HandleEcpayCallbackService implements HandleEcpayCallbackUseCase {

    private static final Logger log = LoggerFactory.getLogger(HandleEcpayCallbackService.class);

    private final EcpayPaymentGatewayClient ecpayPaymentGatewayClient;
    private final PaymentRecordRepository paymentRecordRepository;
    private final PaymentConvergenceService paymentConvergenceService;

    public HandleEcpayCallbackService(EcpayPaymentGatewayClient ecpayPaymentGatewayClient,
            PaymentRecordRepository paymentRecordRepository, PaymentConvergenceService paymentConvergenceService) {
        this.ecpayPaymentGatewayClient = ecpayPaymentGatewayClient;
        this.paymentRecordRepository = paymentRecordRepository;
        this.paymentConvergenceService = paymentConvergenceService;
    }

    @Override
    @Transactional
    public void handle(HandleEcpayCallbackCommand command) {
        if (!ecpayPaymentGatewayClient.verifyCallback(command.params())) {
            throw new InvalidCheckMacValueException();
        }
        String merchantTradeNo = command.params().get("MerchantTradeNo");
        String tradeNo = command.params().get("TradeNo");
        String rtnCode = command.params().get("RtnCode");
        if (!"1".equals(rtnCode)) {
            log.warn("ECPay 通知付款未成功: merchantTradeNo={} rtnCode={}", merchantTradeNo, rtnCode);
            return;
        }

        PaymentRecord record = paymentRecordRepository.findByEcpayMerchantTradeNo(merchantTradeNo).orElse(null);
        if (record == null) {
            log.warn("ECPay callback 找不到對應的 payment_record: merchantTradeNo={}", merchantTradeNo);
            return;
        }
        if (!paymentConvergenceService.convergeToPaid(record, tradeNo)) {
            log.info("ECPay callback 重複通知或訂單已非 PENDING，冪等略過: orderSn={}", record.getOrderSn());
        }
    }
}
