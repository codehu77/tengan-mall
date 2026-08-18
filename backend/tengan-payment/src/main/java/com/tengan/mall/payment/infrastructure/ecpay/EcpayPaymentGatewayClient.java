package com.tengan.mall.payment.infrastructure.ecpay;

import com.tengan.mall.payment.application.payment.EcpayFormData;
import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.LinkedHashMap;
import java.util.Map;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

/** 組出 ECPay AioCheckOut 表單參數 + 驗證 callback 簽章，都靠 {@link EcpayCheckMacValueCalculator} 這個純函式。 */
@Component
public class EcpayPaymentGatewayClient {

    private static final DateTimeFormatter TRADE_DATE_FORMAT = DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm:ss");

    /**
     * 訂閱扣款週期，目前全站唯一使用的組合。改這兩個值時 {@link #nextPaidUntil} 會自動跟著變，
     * 不用另外去改 paidUntil 的延長邏輯——單一事實來源，避免兩邊各存一份定義互相漂移。
     */
    private static final String PERIOD_TYPE = "D";
    private static final int FREQUENCY = 1;

    private final String merchantId;
    private final String hashKey;
    private final String hashIv;
    private final String actionUrl;
    private final String returnUrl;
    private final String subscriptionReturnUrl;
    private final String periodReturnUrl;
    private final String clientBackUrl;
    private final String subscriptionClientBackUrl;

    public EcpayPaymentGatewayClient(@Value("${tengan.ecpay.merchant-id}") String merchantId,
            @Value("${tengan.ecpay.hash-key}") String hashKey, @Value("${tengan.ecpay.hash-iv}") String hashIv,
            @Value("${tengan.ecpay.action-url}") String actionUrl,
            @Value("${tengan.payment.ecpay-return-url}") String returnUrl,
            @Value("${tengan.payment.ecpay-subscription-return-url}") String subscriptionReturnUrl,
            @Value("${tengan.payment.ecpay-period-return-url}") String periodReturnUrl,
            @Value("${tengan.payment.client-back-url}") String clientBackUrl,
            @Value("${tengan.payment.subscription-client-back-url}") String subscriptionClientBackUrl) {
        this.merchantId = merchantId;
        this.hashKey = hashKey;
        this.hashIv = hashIv;
        this.actionUrl = actionUrl;
        this.returnUrl = returnUrl;
        this.subscriptionReturnUrl = subscriptionReturnUrl;
        this.periodReturnUrl = periodReturnUrl;
        this.clientBackUrl = clientBackUrl;
        this.subscriptionClientBackUrl = subscriptionClientBackUrl;
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

    /**
     * Phase 8.5：訂閱式定期定額付款。ExecTimes 定案設成 999（D/M 週期上限）——模型化成「持續扣款
     * 直到使用者取消」，不是固定期數的訂閱方案。CheckMacValue 計算完全重用同一個純函式，不需要
     * 知道多了哪些欄位。
     */
    public EcpayFormData buildPeriodicCreditCardForm(String subscriptionMerchantTradeNo, BigDecimal periodAmount,
            String itemName) {
        Map<String, String> params = new LinkedHashMap<>();
        params.put("MerchantID", merchantId);
        params.put("MerchantTradeNo", subscriptionMerchantTradeNo);
        params.put("MerchantTradeDate", LocalDateTime.now().format(TRADE_DATE_FORMAT));
        params.put("PaymentType", "aio");
        params.put("TotalAmount", String.valueOf(periodAmount.intValueExact()));
        params.put("TradeDesc", "Tengan Mall subscription " + subscriptionMerchantTradeNo);
        params.put("ItemName", itemName);
        params.put("ReturnURL", subscriptionReturnUrl);
        params.put("ClientBackURL", subscriptionClientBackUrl);
        params.put("ChoosePayment", "Credit");
        params.put("EncryptType", "1");
        params.put("PeriodAmount", String.valueOf(periodAmount.intValueExact()));
        params.put("PeriodType", PERIOD_TYPE);
        params.put("Frequency", String.valueOf(FREQUENCY));
        params.put("ExecTimes", "999");
        params.put("PeriodReturnURL", periodReturnUrl);

        String checkMacValue = EcpayCheckMacValueCalculator.calculate(params, hashKey, hashIv);
        params.put("CheckMacValue", checkMacValue);
        return new EcpayFormData(actionUrl, params);
    }

    /**
     * 依 {@link #PERIOD_TYPE}/{@link #FREQUENCY} 把某一期扣款成功的 processDate 往後推一個週期，
     * 算出新的 paidUntil。用 {@link ZonedDateTime#plusMonths}/{@code plusYears} 而不是固定天數相加，
     * 才不會在月份天數不一致（28~31天）時累積誤差。
     */
    public Instant nextPaidUntil(Instant processDate) {
        ZonedDateTime zoned = processDate.atZone(ZoneId.systemDefault());
        ZonedDateTime next = switch (PERIOD_TYPE) {
            case "D" -> zoned.plusDays(FREQUENCY);
            case "M" -> zoned.plusMonths(FREQUENCY);
            case "Y" -> zoned.plusYears(FREQUENCY);
            default -> throw new IllegalStateException("不支援的 PeriodType: " + PERIOD_TYPE);
        };
        return next.toInstant();
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
