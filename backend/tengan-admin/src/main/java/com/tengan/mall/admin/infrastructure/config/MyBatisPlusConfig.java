package com.tengan.mall.admin.infrastructure.config;

import com.baomidou.mybatisplus.annotation.DbType;
import com.baomidou.mybatisplus.extension.plugins.MybatisPlusInterceptor;
import com.baomidou.mybatisplus.extension.plugins.inner.PaginationInnerInterceptor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * 沒有這個 interceptor，MyBatis-Plus 的 {@code selectPage(Page, wrapper)} 不會真的組出
 * LIMIT/OFFSET，會安靜地回傳整包未分頁的結果——不會報錯，只是分頁形同虛設（症狀：換第二頁
 * 顯示的內容跟第一頁一樣）。{@code AdminUserRepositoryImpl.findPage} 跟
 * {@code OperLogRepositoryImpl.search} 都靠這顆 bean 才會是真的分頁查詢。
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
