package com.tengan.mall.order;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

/** @EnableScheduling 是 Phase 8 新增——PointsGrantScheduler 掃描鑑賞期過期的已完成訂單。 */
@SpringBootApplication
@MapperScan("com.tengan.mall.order.infrastructure.persistence")
@EnableScheduling
public class TenganOrderApplication {

    public static void main(String[] args) {
        SpringApplication.run(TenganOrderApplication.class, args);
    }
}
