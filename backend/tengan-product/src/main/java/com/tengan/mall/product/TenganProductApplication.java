package com.tengan.mall.product;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@MapperScan("com.tengan.mall.product.infrastructure.persistence")
public class TenganProductApplication {

    public static void main(String[] args) {
        SpringApplication.run(TenganProductApplication.class, args);
    }
}
