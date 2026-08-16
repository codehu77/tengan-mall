package com.tengan.mall.payment;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@MapperScan("com.tengan.mall.payment.infrastructure.persistence")
public class TenganPaymentApplication {

    public static void main(String[] args) {
        SpringApplication.run(TenganPaymentApplication.class, args);
    }
}
