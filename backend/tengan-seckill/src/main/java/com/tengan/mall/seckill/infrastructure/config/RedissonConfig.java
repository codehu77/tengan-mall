package com.tengan.mall.seckill.infrastructure.config;

import org.redisson.Redisson;
import org.redisson.api.RedissonClient;
import org.redisson.config.Config;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * 整個專案第一次引入 Redisson（RSemaphore 分散式信號量），沿用既有 spring.data.redis.host/port/password
 * 這幾個 property，不另開一套設定命名空間——這幾個值本來就是同一個 Redis 實例的連線資訊，跟
 * StringRedisTemplate 用的是同一份，只是 Redisson 需要自己的 client（Spring Data Redis 的
 * RedisConnectionFactory 跟 Redisson 是兩套獨立的用戶端）。
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
