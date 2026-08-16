package com.tengan.mall.wallet;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

/** @EnableScheduling 是全專案第一次真正用到——365 天點數到期用排程掃描，不塞進 RabbitMQ TTL+DLX。 */
@SpringBootApplication
@MapperScan("com.tengan.mall.wallet.infrastructure.persistence")
@EnableScheduling
public class TenganWalletApplication {

    public static void main(String[] args) {
        SpringApplication.run(TenganWalletApplication.class, args);
    }
}
