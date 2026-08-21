package com.tengan.mall.seckill;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@MapperScan("com.tengan.mall.seckill.infrastructure.persistence")
@EnableScheduling
public class TenganSeckillApplication {

    public static void main(String[] args) {
        SpringApplication.run(TenganSeckillApplication.class, args);
    }
}
