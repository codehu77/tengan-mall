package com.tengan.mall.payment;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

/** @EnableScheduling 是 Phase 8.5 新增——SubscriptionExpiryScheduler 在訂閱到期時才降級會員等級。 */
@SpringBootApplication
@MapperScan("com.tengan.mall.payment.infrastructure.persistence")
@EnableScheduling
public class TenganPaymentApplication {

    public static void main(String[] args) {
        SpringApplication.run(TenganPaymentApplication.class, args);
    }
}
