package com.tengan.mall.order;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@MapperScan("com.tengan.mall.order.infrastructure.persistence")
public class TenganOrderApplication {

    public static void main(String[] args) {
        SpringApplication.run(TenganOrderApplication.class, args);
    }
}
