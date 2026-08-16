package com.tengan.mall.payment.infrastructure.linepay;

import java.nio.charset.StandardCharsets;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;
import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;

/**
 * LINE Pay X-LINE-Authorization 純函式（不依賴 Spring，方便單元測試不用起 context）。公式（LINE Pay
 * Online API v3 官方規格，https://developers-pay.line.me/online-api-v3/request-payment ）：
 * {@code Base64(HMAC-SHA256(key=ChannelSecret, message=ChannelSecret+URI+RequestBody(或GET的QueryString)+Nonce))}。
 * 跟 ECPay 不同，這裡不用排序參數、不用自訂 URL encode 規則——直接對「要送出去的 JSON body 原文」整包算，
 * 同時保護「這是你發的」跟「body 沒被竄改過」兩件事。
 *
 * <p>已用 Node crypto 實際跑過驗證（見對話紀錄 linepay_signature_demo.js），
 * {@link LinePaySignatureCalculatorTest} 直接拿那組輸入輸出當測試向量。</p>
 */
public final class LinePaySignatureCalculator {

    private static final String HMAC_ALGORITHM = "HmacSHA256";

    private LinePaySignatureCalculator() {
    }

    public static String calculate(String channelSecret, String uri, String queryOrBody, String nonce) {
        String authMacText = channelSecret + uri + queryOrBody + nonce;
        try {
            Mac mac = Mac.getInstance(HMAC_ALGORITHM);
            mac.init(new SecretKeySpec(channelSecret.getBytes(StandardCharsets.UTF_8), HMAC_ALGORITHM));
            byte[] result = mac.doFinal(authMacText.getBytes(StandardCharsets.UTF_8));
            return Base64.getEncoder().encodeToString(result);
        } catch (NoSuchAlgorithmException | InvalidKeyException e) {
            throw new IllegalStateException("HmacSHA256 簽章計算失敗", e);
        }
    }
}
