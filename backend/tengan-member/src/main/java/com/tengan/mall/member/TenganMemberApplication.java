package com.tengan.mall.member;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@MapperScan("com.tengan.mall.member.infrastructure.persistence")
public class TenganMemberApplication {

    public static void main(String[] args) {
        SpringApplication.run(TenganMemberApplication.class, args);
    }
}
