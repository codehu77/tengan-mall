package com.tengan.mall.cart.infrastructure.inventory;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.web.client.RestClient;

/**
 * 呼叫 tengan-inventory 的 /api/public/** 端點——公開端點不需要 Service JWT，比照
 * ProductClientConfig 的模式關掉 FAIL_ON_UNKNOWN_PROPERTIES，只取用得到的欄位子集。
 */
@Configuration
public class InventoryClientConfig {

    @Bean
    public RestClient inventoryRestClient(@Value("${tengan.inventory.base-url}") String baseUrl) {
        ObjectMapper objectMapper = new ObjectMapper()
                .configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        return RestClient.builder()
                .baseUrl(baseUrl)
                .messageConverters(converters -> converters.add(0,
                        new MappingJackson2HttpMessageConverter(objectMapper)))
                .build();
    }
}
