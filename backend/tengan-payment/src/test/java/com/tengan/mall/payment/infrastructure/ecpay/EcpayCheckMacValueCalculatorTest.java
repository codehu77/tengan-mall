package com.tengan.mall.payment.infrastructure.ecpay;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.LinkedHashMap;
import java.util.Map;
import org.junit.jupiter.api.Test;

/** 測試向量來自對話中用 Node crypto 實際跑過驗證的 ecpay_checkmac_demo.js，不是憑印象編的。 */
class EcpayCheckMacValueCalculatorTest {

    @Test
    void calculate_matchesVerifiedNodeVector() {
        Map<String, String> params = new LinkedHashMap<>();
        params.put("MerchantID", "3002607");
        params.put("MerchantTradeNo", "test20260811001");
        params.put("MerchantTradeDate", "2026/08/11 15:30:00");
        params.put("PaymentType", "aio");
        params.put("TotalAmount", "1000");
        params.put("TradeDesc", "Tengan Mall order");
        params.put("ItemName", "iPhone 17 (256G) - x1");
        params.put("ReturnURL", "https://api.tengan-mall.com/api/partner/payments/callback/ecpay");
        params.put("ChoosePayment", "Credit");
        params.put("EncryptType", "1");

        String result = EcpayCheckMacValueCalculator.calculate(params, "pwFHCqoQZGmho4w6", "EkRm7iFT261dpevs");

        assertEquals("6BA2231D57EFCF5CFBD3FF4B5C38ACEF22FA72FD826AD6B43AE277AB8AD37580", result);
    }
}
