package com.linlee.cloudsnow.common.config;

import cn.dev33.satoken.interceptor.SaInterceptor;
import cn.dev33.satoken.router.SaRouter;
import cn.dev33.satoken.stp.StpUtil;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * Sa-Token配置
 *
 * @author Cloud Snow Pavilion
 * @since 2025-11-22
 */
@Configuration
public class SaTokenConfig implements WebMvcConfigurer {

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        // 注册Sa-Token拦截器
        registry.addInterceptor(new SaInterceptor(handle -> {
            // 指定哪些路由需要登录验证
            SaRouter
                    .match("/**")
                    .notMatch(
                            "/health",
                            "/auth/login",
                            "/auth/admin/login",
                            "/auth/admin/register",
                            "/auth/getSmsCode",
                            "/auth/wxLogin",
                            "/file/**",
                            "/upload_img/**",
                            "/doc.html",
                            "/swagger-resources/**",
                            "/v3/api-docs/**",
                            "/webjars/**",
                            "/favicon.ico"
                    )
                    .check(r -> StpUtil.checkLogin());
        })).addPathPatterns("/**");
    }
}
