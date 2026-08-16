package com.tengan.mall.payment.infrastructure.linepay;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

/** 測試向量來自對話中用 Node crypto 實際跑過驗證的 linepay_signature_demo.js，不是憑印象編的。 */
class LinePaySignatureCalculatorTest {

    @Test
    void calculate_matchesVerifiedNodeVector() {
        String channelSecret = "a917ab6a2367b536f8e5a6e2977e06f4";
        String uri = "/v3/payments/request";
        String requestBody = "{\"amount\":1000,\"currency\":\"TWD\",\"orderId\":\"test20260811001\","
                + "\"packages\":[{\"id\":\"pkg1\",\"amount\":1000,\"products\":[{\"name\":\"iPhone 17 (256G)\","
                + "\"quantity\":1,\"price\":1000}]}],\"redirectUrls\":{"
                + "\"confirmUrl\":\"https://www.tengan-mall.com/order/pay?orderSn=test20260811001\","
                + "\"cancelUrl\":\"https://www.tengan-mall.com/order/pay?orderSn=test20260811001&cancel=1\"}}";
        String nonce = "44453d45-768e-40e8-8349-748e797c450f";

        String result = LinePaySignatureCalculator.calculate(channelSecret, uri, requestBody, nonce);

        assertEquals("B8uTnH1nxISzBZGUJF7btagiEiOwIvdRuIYzyvbsM00=", result);
    }
}
