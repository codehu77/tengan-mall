package com.tengan.mall.admin;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;

@SpringBootApplication
@MapperScan("com.tengan.mall.admin.infrastructure.persistence")
@EnableMethodSecurity
public class TenganAdminApplication {

    public static void main(String[] args) {
        SpringApplication.run(TenganAdminApplication.class, args);
    }
}
