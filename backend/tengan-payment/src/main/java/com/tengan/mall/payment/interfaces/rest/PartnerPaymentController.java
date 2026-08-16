package com.tengan.mall.payment.interfaces.rest;

import com.tengan.mall.payment.application.webhook.HandleEcpayCallbackCommand;
import com.tengan.mall.payment.application.webhook.HandleEcpayCallbackUseCase;
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

    public PartnerPaymentController(HandleEcpayCallbackUseCase handleEcpayCallbackUseCase) {
        this.handleEcpayCallbackUseCase = handleEcpayCallbackUseCase;
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
}
