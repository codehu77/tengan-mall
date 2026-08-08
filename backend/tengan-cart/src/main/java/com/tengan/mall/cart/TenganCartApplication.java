package com.tengan.mall.cart;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@MapperScan("com.tengan.mall.cart.infrastructure.persistence")
public class TenganCartApplication {

    public static void main(String[] args) {
        SpringApplication.run(TenganCartApplication.class, args);
    }
}
