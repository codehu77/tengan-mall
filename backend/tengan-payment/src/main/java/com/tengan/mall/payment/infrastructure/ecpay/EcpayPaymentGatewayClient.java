package com.tengan.mall.payment.infrastructure.ecpay;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.tengan.mall.payment.application.payment.EcpayFormData;
import com.tengan.mall.payment.application.payment.EcpayTradeQueryResult;
import com.tengan.mall.payment.application.subscription.EcpayPeriodQueryResult;
import com.tengan.mall.payment.domain.exception.PaymentGatewayException;
import java.math.BigDecimal;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.stream.Collectors;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientException;

/** 組出 ECPay AioCheckOut 表單參數 + 驗證 callback 簽章 + 主動查詢訂單/訂閱狀態，都靠 {@link EcpayCheckMacValueCalculator} 這個純函式算簽章。 */
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
    private final String queryTradeUrl;
    private final String queryPeriodUrl;
    private final RestClient restClient;
    private final ObjectMapper objectMapper = new ObjectMapper();

    public EcpayPaymentGatewayClient(@Value("${tengan.ecpay.merchant-id}") String merchantId,
            @Value("${tengan.ecpay.hash-key}") String hashKey, @Value("${tengan.ecpay.hash-iv}") String hashIv,
            @Value("${tengan.ecpay.action-url}") String actionUrl,
            @Value("${tengan.payment.ecpay-return-url}") String returnUrl,
            @Value("${tengan.payment.ecpay-subscription-return-url}") String subscriptionReturnUrl,
            @Value("${tengan.payment.ecpay-period-return-url}") String periodReturnUrl,
            @Value("${tengan.payment.client-back-url}") String clientBackUrl,
            @Value("${tengan.payment.subscription-client-back-url}") String subscriptionClientBackUrl,
            @Value("${tengan.ecpay.query-trade-url:https://payment-stage.ecpay.com.tw/Cashier/QueryTradeInfo/V5}") String queryTradeUrl,
            @Value("${tengan.ecpay.query-period-url:https://payment-stage.ecpay.com.tw/Cashier/QueryCreditCardPeriodInfo}") String queryPeriodUrl) {
        this.merchantId = merchantId;
        this.hashKey = hashKey;
        this.hashIv = hashIv;
        this.actionUrl = actionUrl;
        this.returnUrl = returnUrl;
        this.subscriptionReturnUrl = subscriptionReturnUrl;
        this.periodReturnUrl = periodReturnUrl;
        this.clientBackUrl = clientBackUrl;
        this.subscriptionClientBackUrl = subscriptionClientBackUrl;
        this.queryTradeUrl = queryTradeUrl;
        this.queryPeriodUrl = queryPeriodUrl;
        this.restClient = RestClient.create();
    }

    /**
     * @param merchantTradeNo 這次付款嘗試專屬的 ECPay MerchantTradeNo（每次重試都不同，見
     *                        {@code InitiatePaymentService.generateMerchantTradeNo()}），不是 orderSn。
     * @param orderSn         純粹用來組 TradeDesc/ClientBackURL，前端靠這個知道要導回哪張訂單。
     */
    public EcpayFormData buildCreditCardForm(String merchantTradeNo, String orderSn, BigDecimal amount,
            String itemName) {
        Map<String, String> params = new LinkedHashMap<>();
        params.put("MerchantID", merchantId);
        params.put("MerchantTradeNo", merchantTradeNo);
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
     * 查詢一般訂單的真實付款狀態（{@code POST /Cashier/QueryTradeInfo/V5}），供 credit_card 重試/
     * 排程式查帳共用。回應是 form-encoded 字串（不是 JSON），且帶 CheckMacValue，收到後一併驗簽。
     */
    public EcpayTradeQueryResult queryTrade(String merchantTradeNo) {
        Map<String, String> params = buildQueryParams(merchantTradeNo);
        String responseBody = postForm(queryTradeUrl, params);
        Map<String, String> parsed = parseFormEncoded(responseBody);
        if (parsed.containsKey("CheckMacValue") && !verifyCallback(parsed)) {
            throw new PaymentGatewayException("ECPay 查詢訂單回應簽章驗證失敗: merchantTradeNo=" + merchantTradeNo);
        }
        String tradeStatus = parsed.get("TradeStatus");
        if (tradeStatus == null) {
            throw new PaymentGatewayException("ECPay 查詢訂單回應格式異常: " + responseBody);
        }
        return new EcpayTradeQueryResult(tradeStatus, parsed.get("TradeNo"), parsed.get("PaymentDate"));
    }

    /**
     * 查詢訂閱首期是否真的授權成功（{@code POST /Cashier/QueryCreditCardPeriodInfo}）。回應是 JSON，
     * 這裡只取「首次授權」相關欄位（見 {@link EcpayPeriodQueryResult} javadoc），不解析 ExecLog 陣列
     * ——同步查帳目前只用來確認訂閱首期，不處理第二期以後。
     */
    public EcpayPeriodQueryResult queryPeriod(String merchantTradeNo) {
        Map<String, String> params = buildQueryParams(merchantTradeNo);
        String responseBody = postForm(queryPeriodUrl, params);
        Map<String, Object> json = parseJson(responseBody);
        if (!json.containsKey("RtnCode")) {
            throw new PaymentGatewayException("ECPay 訂閱查詢回應格式異常: " + responseBody);
        }
        int rtnCode = parseIntOrZero(json.get("RtnCode"));
        int totalSuccessTimes = parseIntOrZero(json.get("TotalSuccessTimes"));
        boolean succeeded = rtnCode == 1 && totalSuccessTimes >= 1;
        String gwsr = stringOrNull(json.get("gwsr"));
        String tradeNo = stringOrNull(json.get("TradeNo"));
        BigDecimal amount = parseAmountOrZero(stringOrNull(json.get("amount")));
        Instant processDate = parseProcessDate(stringOrNull(json.get("process_date")));
        return new EcpayPeriodQueryResult(succeeded, gwsr, tradeNo, amount, totalSuccessTimes, processDate);
    }

    private Map<String, String> buildQueryParams(String merchantTradeNo) {
        Map<String, String> params = new LinkedHashMap<>();
        params.put("MerchantID", merchantId);
        params.put("MerchantTradeNo", merchantTradeNo);
        params.put("TimeStamp", String.valueOf(Instant.now().getEpochSecond()));
        String checkMacValue = EcpayCheckMacValueCalculator.calculate(params, hashKey, hashIv);
        params.put("CheckMacValue", checkMacValue);
        return params;
    }

    private String postForm(String url, Map<String, String> params) {
        String body = params.entrySet().stream()
                .map(e -> URLEncoder.encode(e.getKey(), StandardCharsets.UTF_8) + "="
                        + URLEncoder.encode(e.getValue(), StandardCharsets.UTF_8))
                .collect(Collectors.joining("&"));
        try {
            return restClient.post().uri(url).contentType(MediaType.APPLICATION_FORM_URLENCODED).body(body)
                    .retrieve().body(String.class);
        } catch (RestClientException e) {
            throw new PaymentGatewayException("呼叫 ECPay 查詢 API 失敗: url=" + url, e);
        }
    }

    private Map<String, String> parseFormEncoded(String body) {
        Map<String, String> result = new LinkedHashMap<>();
        if (body == null || body.isBlank()) {
            return result;
        }
        for (String pair : body.split("&")) {
            int idx = pair.indexOf('=');
            if (idx < 0) {
                continue;
            }
            result.put(URLDecoder.decode(pair.substring(0, idx), StandardCharsets.UTF_8),
                    URLDecoder.decode(pair.substring(idx + 1), StandardCharsets.UTF_8));
        }
        return result;
    }

    private Map<String, Object> parseJson(String body) {
        try {
            return objectMapper.readValue(body, new TypeReference<Map<String, Object>>() {
            });
        } catch (JsonProcessingException | RuntimeException e) {
            throw new PaymentGatewayException("ECPay 查詢回應 JSON 解析失敗: " + body, e);
        }
    }

    private String stringOrNull(Object value) {
        return value == null ? null : String.valueOf(value);
    }

    private int parseIntOrZero(Object value) {
        try {
            return value == null ? 0 : Integer.parseInt(String.valueOf(value));
        } catch (NumberFormatException e) {
            return 0;
        }
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

    private BigDecimal parseAmountOrZero(String value) {
        try {
            return value == null ? BigDecimal.ZERO : new BigDecimal(value);
        } catch (NumberFormatException e) {
            return BigDecimal.ZERO;
        }
    }

    private Instant parseProcessDate(String value) {
        if (value == null || value.isBlank()) {
            return Instant.now();
        }
        try {
            return LocalDateTime.parse(value, TRADE_DATE_FORMAT).atZone(ZoneId.systemDefault()).toInstant();
        } catch (RuntimeException e) {
            return Instant.now();
        }
    }
}
