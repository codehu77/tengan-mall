package com.tengan.mall.inventory;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@MapperScan("com.tengan.mall.inventory.infrastructure.persistence")
@EnableScheduling
public class TenganInventoryApplication {

    public static void main(String[] args) {
        SpringApplication.run(TenganInventoryApplication.class, args);
    }
}
