package com.linlee.cloudsnow.common.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * Web配置
 *
 * @author Cloud Snow Pavilion
 * @since 2025-11-22
 */
@Configuration
public class WebConfig implements WebMvcConfigurer {

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        // 配置图片访问路径映射
        registry.addResourceHandler("/upload_img/**")
                .addResourceLocations("file:R:/Code_Repository/Cloud_Snow_Pavilion/upload_img/");
    }
}
