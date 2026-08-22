package com.tengan.mall.seckill.infrastructure.config;

import com.baomidou.mybatisplus.annotation.DbType;
import com.baomidou.mybatisplus.extension.plugins.MybatisPlusInterceptor;
import com.baomidou.mybatisplus.extension.plugins.inner.PaginationInnerInterceptor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * 沒有這個 interceptor，MyBatis-Plus 的 selectPage(Page, wrapper) 不會真的組出 LIMIT/OFFSET
 * （比照 tengan-member/tengan-admin/tengan-order/tengan-inventory 同款設定）。
 * SeckillActivityRepositoryImpl.selectPage 靠這顆 bean 才是真的分頁查詢。
 */
@Configuration
public class MyBatisPlusConfig {

    @Bean
    public MybatisPlusInterceptor mybatisPlusInterceptor() {
        MybatisPlusInterceptor interceptor = new MybatisPlusInterceptor();
        interceptor.addInnerInterceptor(new PaginationInnerInterceptor(DbType.MYSQL));
        return interceptor;
    }
}
