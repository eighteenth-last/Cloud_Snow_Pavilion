package com.linlee.cloudsnow.common.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
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
        // Windows路径需要使用file:/// + 完整路径，路径分隔符可以使用/或\\
        registry.addResourceHandler("/upload_img/**")
                .addResourceLocations("file:R:/Code_Repository/Cloud_Snow_Pavilion/upload_img/");
    }

    @Override
    public void addCorsMappings(CorsRegistry registry) {
        // 配置跨域
        registry.addMapping("/**")
                .allowedOriginPatterns("*")
                .allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS")
                .allowedHeaders("*")
                .allowCredentials(true)
                .maxAge(3600);
    }
}
