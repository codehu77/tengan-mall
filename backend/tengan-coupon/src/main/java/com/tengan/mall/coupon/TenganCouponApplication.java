package com.tengan.mall.coupon;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@MapperScan("com.tengan.mall.coupon.infrastructure.persistence")
public class TenganCouponApplication {

    public static void main(String[] args) {
        SpringApplication.run(TenganCouponApplication.class, args);
    }
}
