package com.tengan.mall.media;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

/** @EnableScheduling 是這個服務第一次用到——MediaAssetCleanupScheduler 定期回收孤兒媒體物件。 */
@SpringBootApplication
@MapperScan("com.tengan.mall.media.infrastructure.persistence")
@EnableScheduling
public class TenganMediaApplication {

    public static void main(String[] args) {
        SpringApplication.run(TenganMediaApplication.class, args);
    }
}
