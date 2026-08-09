package com.tengan.mall.admin.infrastructure.web;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * 讓 LocalFileStorageAdapter 寫進磁碟的頭像檔案可以直接用 URL 讀到。這條路徑同時要在
 * tengan-gateway 的 UserJwtFilter 和本服務的 SecurityConfig 放行，理由見 SecurityConfig 註解。
 */
@Configuration
public class WebMvcConfig implements WebMvcConfigurer {

    private final String uploadDir;

    public WebMvcConfig(@Value("${app.upload.dir:./data/admin-uploads}") String uploadDir) {
        this.uploadDir = uploadDir;
    }

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        registry.addResourceHandler("/api/admin/static/**").addResourceLocations("file:" + uploadDir + "/");
    }
}
