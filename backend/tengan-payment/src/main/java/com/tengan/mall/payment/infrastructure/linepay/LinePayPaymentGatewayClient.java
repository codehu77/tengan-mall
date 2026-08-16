package com.tengan.mall.payment.infrastructure.linepay;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.tengan.mall.payment.application.payment.LinePayRequestResult;
import com.tengan.mall.payment.infrastructure.linepay.dto.LinePayConfirmBody;
import com.tengan.mall.payment.infrastructure.linepay.dto.LinePayConfirmResponse;
import com.tengan.mall.payment.infrastructure.linepay.dto.LinePayRequestBody;
import com.tengan.mall.payment.infrastructure.linepay.dto.LinePayRequestResponse;
import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

/**
 * LINE Pay Online API v3 呼叫端。跟 ECPay 不一樣，這裡用純 RestClient（不是 OAuth2 client-credentials）
 * ——LINE Pay 認證是逐請求 HMAC header（見 {@link LinePaySignatureCalculator}），沒有 bearer token
 * 這種東西，每次呼叫都要重算簽章。JSON body 先手動序列化成字串再送出（RestClient 直接 .body(String)
 * 讓 StringHttpMessageConverter 原封不動送出，不讓 Jackson 二次序列化），確保簽章計算的字串跟實際送出的
 * bytes 完全一致。
 */
@Component
public class LinePayPaymentGatewayClient {

    private final RestClient restClient;
    private final ObjectMapper objectMapper;
    private final String channelId;
    private final String channelSecret;

    public LinePayPaymentGatewayClient(@Value("${tengan.linepay.channel-id}") String channelId,
            @Value("${tengan.linepay.channel-secret}") String channelSecret,
            @Value("${tengan.linepay.api-base-url}") String apiBaseUrl) {
        this.channelId = channelId;
        this.channelSecret = channelSecret;
        this.restClient = RestClient.builder().baseUrl(apiBaseUrl).build();
        this.objectMapper = new ObjectMapper().configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
    }

    public LinePayRequestResult request(String orderSn, BigDecimal amount, String itemName, String confirmUrl,
            String cancelUrl) {
        String uri = "/v3/payments/request";
        long amountLong = amount.longValueExact();
        LinePayRequestBody body = new LinePayRequestBody(amountLong, "TWD", orderSn,
                List.of(new LinePayRequestBody.Package("pkg1", amountLong,
                        List.of(new LinePayRequestBody.Product(itemName, 1, amountLong)))),
                new LinePayRequestBody.RedirectUrls(confirmUrl, cancelUrl));
        String jsonBody = writeJson(body);
        String nonce = UUID.randomUUID().toString();
        String signature = LinePaySignatureCalculator.calculate(channelSecret, uri, jsonBody, nonce);

        LinePayRequestResponse response = restClient.post().uri(uri).contentType(MediaType.APPLICATION_JSON)
                .header("X-LINE-ChannelId", channelId).header("X-LINE-Authorization-Nonce", nonce)
                .header("X-LINE-Authorization", signature).body(jsonBody).retrieve()
                .body(LinePayRequestResponse.class);
        if (response == null || !"0000".equals(response.returnCode()) || response.info() == null) {
            throw new IllegalStateException("LINE Pay Request API 失敗: "
                    + (response == null ? "no response" : response.returnCode() + " " + response.returnMessage()));
        }
        return new LinePayRequestResult(String.valueOf(response.info().transactionId()),
                response.info().paymentUrl().web());
    }

    public boolean confirm(String transactionId, BigDecimal amount) {
        String uri = "/v3/payments/" + transactionId + "/confirm";
        LinePayConfirmBody body = new LinePayConfirmBody(amount.longValueExact(), "TWD");
        String jsonBody = writeJson(body);
        String nonce = UUID.randomUUID().toString();
        String signature = LinePaySignatureCalculator.calculate(channelSecret, uri, jsonBody, nonce);

        LinePayConfirmResponse response = restClient.post().uri(uri).contentType(MediaType.APPLICATION_JSON)
                .header("X-LINE-ChannelId", channelId).header("X-LINE-Authorization-Nonce", nonce)
                .header("X-LINE-Authorization", signature).body(jsonBody).retrieve()
                .body(LinePayConfirmResponse.class);
        return response != null && response.isSuccess();
    }

    private String writeJson(Object body) {
        try {
            return objectMapper.writeValueAsString(body);
        } catch (JsonProcessingException e) {
            throw new IllegalStateException("LINE Pay 請求序列化失敗", e);
        }
    }
}
