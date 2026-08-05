package com.tengan.mall.admin.infrastructure.search;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestClient;

/** authorizedClientManager 沿用 ProductClientConfig 已經定義的那顆 bean，這裡只多開一個 RestClient。 */
@Configuration
public class SearchClientConfig {

    @Bean
    public RestClient searchRestClient(@Value("${tengan.search.base-url}") String baseUrl) {
        return RestClient.builder().baseUrl(baseUrl).build();
    }
}
