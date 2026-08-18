package com.tengan.mall.payment.interfaces.rest;

import com.tengan.mall.payment.application.webhook.HandleEcpayCallbackCommand;
import com.tengan.mall.payment.application.webhook.HandleEcpayCallbackUseCase;
import com.tengan.mall.payment.application.webhook.HandlePeriodCallbackCommand;
import com.tengan.mall.payment.application.webhook.HandlePeriodCallbackUseCase;
import com.tengan.mall.payment.application.webhook.HandleSubscriptionReturnCallbackCommand;
import com.tengan.mall.payment.application.webhook.HandleSubscriptionReturnCallbackUseCase;
import java.util.Map;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * ECPay 的 server-to-server callback，走 SecurityConfig 的 partnerChain（permitAll，見該類 javadoc）。
 * 不管驗簽成功或失敗都要回 200（ECPay 官方要求收到 1|OK 才視為通知送達，否則會一直重試轟炸），
 * 驗簽失敗只記錄不拋 500，避免觸發 ECPay 無限重試。
 */
@RestController
@RequestMapping("/api/partner/payments")
public class PartnerPaymentController {

    private static final Logger log = LoggerFactory.getLogger(PartnerPaymentController.class);

    private final HandleEcpayCallbackUseCase handleEcpayCallbackUseCase;
    private final HandlePeriodCallbackUseCase handlePeriodCallbackUseCase;
    private final HandleSubscriptionReturnCallbackUseCase handleSubscriptionReturnCallbackUseCase;

    public PartnerPaymentController(HandleEcpayCallbackUseCase handleEcpayCallbackUseCase,
            HandlePeriodCallbackUseCase handlePeriodCallbackUseCase,
            HandleSubscriptionReturnCallbackUseCase handleSubscriptionReturnCallbackUseCase) {
        this.handleEcpayCallbackUseCase = handleEcpayCallbackUseCase;
        this.handlePeriodCallbackUseCase = handlePeriodCallbackUseCase;
        this.handleSubscriptionReturnCallbackUseCase = handleSubscriptionReturnCallbackUseCase;
    }

    // 刻意不用 produces 屬性：那會讓 Spring 在進 controller 前用請求的 Accept header 做內容協商，
    // ECPay 的 server 沒送 Accept: text/plain 就會直接被 406 擋掉、handler 完全不會執行。
    // 改用 ResponseEntity 自己指定 Content-Type，不受呼叫端 Accept header 影響。
    @PostMapping("/callback/ecpay")
    public ResponseEntity<String> ecpayCallback(@RequestParam Map<String, String> params) {
        try {
            handleEcpayCallbackUseCase.handle(new HandleEcpayCallbackCommand(params));
        } catch (RuntimeException e) {
            log.error("處理 ECPay callback 失敗: {}", params.get("MerchantTradeNo"), e);
        }
        return ResponseEntity.ok().contentType(MediaType.TEXT_PLAIN).body("1|OK");
    }

    /** Phase 8.5：訂閱每一期（含第一期）的定期定額扣款通知，可能延遲到隔天才送達。 */
    @PostMapping("/callback/ecpay-period")
    public ResponseEntity<String> ecpayPeriodCallback(@RequestParam Map<String, String> params) {
        try {
            handlePeriodCallbackUseCase.handle(new HandlePeriodCallbackCommand(params));
        } catch (RuntimeException e) {
            log.error("處理 ECPay 定期定額 callback 失敗: {}", params.get("MerchantTradeNo"), e);
        }
        return ResponseEntity.ok().contentType(MediaType.TEXT_PLAIN).body("1|OK");
    }

    /**
     * Phase 8.5 補強：訂閱第一期的授權結果，AioCheckOut 當下同步送回，跟一般訂單的 ReturnURL
     * 是不同的 use case，故意分開一支端點，不共用 {@code /callback/ecpay}。
     */
    @PostMapping("/callback/ecpay-subscription")
    public ResponseEntity<String> ecpaySubscriptionReturnCallback(@RequestParam Map<String, String> params) {
        try {
            handleSubscriptionReturnCallbackUseCase.handle(new HandleSubscriptionReturnCallbackCommand(params));
        } catch (RuntimeException e) {
            log.error("處理 ECPay 訂閱首刷 callback 失敗: {}", params.get("MerchantTradeNo"), e);
        }
        return ResponseEntity.ok().contentType(MediaType.TEXT_PLAIN).body("1|OK");
    }
}
