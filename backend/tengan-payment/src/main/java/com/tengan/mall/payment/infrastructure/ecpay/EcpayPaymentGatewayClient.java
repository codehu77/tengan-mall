package com.tengan.mall.payment.infrastructure.ecpay;

import com.tengan.mall.payment.application.payment.EcpayFormData;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.LinkedHashMap;
import java.util.Map;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

/** 組出 ECPay AioCheckOut 表單參數 + 驗證 callback 簽章，都靠 {@link EcpayCheckMacValueCalculator} 這個純函式。 */
@Component
public class EcpayPaymentGatewayClient {

    private static final DateTimeFormatter TRADE_DATE_FORMAT = DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm:ss");

    private final String merchantId;
    private final String hashKey;
    private final String hashIv;
    private final String actionUrl;
    private final String returnUrl;
    private final String clientBackUrl;

    public EcpayPaymentGatewayClient(@Value("${tengan.ecpay.merchant-id}") String merchantId,
            @Value("${tengan.ecpay.hash-key}") String hashKey, @Value("${tengan.ecpay.hash-iv}") String hashIv,
            @Value("${tengan.ecpay.action-url}") String actionUrl,
            @Value("${tengan.payment.ecpay-return-url}") String returnUrl,
            @Value("${tengan.payment.client-back-url}") String clientBackUrl) {
        this.merchantId = merchantId;
        this.hashKey = hashKey;
        this.hashIv = hashIv;
        this.actionUrl = actionUrl;
        this.returnUrl = returnUrl;
        this.clientBackUrl = clientBackUrl;
    }

    public EcpayFormData buildCreditCardForm(String orderSn, BigDecimal amount, String itemName) {
        Map<String, String> params = new LinkedHashMap<>();
        params.put("MerchantID", merchantId);
        params.put("MerchantTradeNo", orderSn);
        params.put("MerchantTradeDate", LocalDateTime.now().format(TRADE_DATE_FORMAT));
        params.put("PaymentType", "aio");
        params.put("TotalAmount", String.valueOf(amount.intValueExact()));
        params.put("TradeDesc", "Tengan Mall order " + orderSn);
        params.put("ItemName", itemName);
        params.put("ReturnURL", returnUrl);
        params.put("ClientBackURL", clientBackUrl + "?orderSn=" + orderSn);
        params.put("ChoosePayment", "Credit");
        params.put("EncryptType", "1");

        String checkMacValue = EcpayCheckMacValueCalculator.calculate(params, hashKey, hashIv);
        params.put("CheckMacValue", checkMacValue);
        return new EcpayFormData(actionUrl, params);
    }

    /** params 應包含 callback 收到的全部欄位（含 CheckMacValue 本身，這裡會自動剔除再重算比對）。 */
    public boolean verifyCallback(Map<String, String> receivedParams) {
        String received = receivedParams.get("CheckMacValue");
        if (received == null) {
            return false;
        }
        Map<String, String> toVerify = new LinkedHashMap<>(receivedParams);
        toVerify.remove("CheckMacValue");
        String computed = EcpayCheckMacValueCalculator.calculate(toVerify, hashKey, hashIv);
        return computed.equalsIgnoreCase(received);
    }
}
