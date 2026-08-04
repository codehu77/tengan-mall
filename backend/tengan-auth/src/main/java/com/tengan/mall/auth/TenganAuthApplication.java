package com.tengan.mall.auth;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@MapperScan("com.tengan.mall.auth.infrastructure.persistence")
public class TenganAuthApplication {

    public static void main(String[] args) {
        SpringApplication.run(TenganAuthApplication.class, args);
    }
}
