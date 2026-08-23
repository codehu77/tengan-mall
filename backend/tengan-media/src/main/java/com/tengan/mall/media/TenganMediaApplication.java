package com.tengan.mall.media;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@MapperScan("com.tengan.mall.media.infrastructure.persistence")
public class TenganMediaApplication {

    public static void main(String[] args) {
        SpringApplication.run(TenganMediaApplication.class, args);
    }
}
