package com.tengan.mall.inventory.infrastructure.config;

import org.redisson.Redisson;
import org.redisson.api.RedissonClient;
import org.redisson.config.Config;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * 這個服務第一次引入 Redisson（RSemaphore 分散式信號量，即將開賣 Phase B 流量閘門用），完全比照
 * tengan-seckill 既有的 RedissonConfig 寫法，沿用同一組 spring.data.redis.host/port/password
 * property（同一個 Redis 實例，只是 Redisson 需要自己的 client，跟 Spring Data Redis 的
 * RedisConnectionFactory 是兩套獨立的用戶端）。
 */
@Configuration
public class RedissonConfig {

    @Bean(destroyMethod = "shutdown")
    public RedissonClient redissonClient(@Value("${spring.data.redis.host}") String host,
            @Value("${spring.data.redis.port}") int port, @Value("${spring.data.redis.password}") String password) {
        Config config = new Config();
        config.useSingleServer()
                .setAddress("redis://" + host + ":" + port)
                .setPassword(password);
        return Redisson.create(config);
    }
}
