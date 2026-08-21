package com.tengan.mall.snowflake;

import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * 每個服務只要加這個 starter 依賴，就能直接注入 {@link SnowflakeIdGenerator}，不用各自重寫演算法
 * （見 docs/ddd-standards.md 第九節例外條款）。{@code @ConditionalOnMissingBean} 讓服務仍然可以
 * 自行覆蓋這顆 Bean（目前沒有服務這麼做，保留彈性）。
 */
@Configuration
@EnableConfigurationProperties(TenganSnowflakeProperties.class)
public class SnowflakeAutoConfiguration {

    @Bean
    @ConditionalOnMissingBean
    public SnowflakeIdGenerator snowflakeIdGenerator(TenganSnowflakeProperties properties) {
        return new SnowflakeIdGenerator(properties.getDatacenterId(), properties.getWorkerId());
    }
}
