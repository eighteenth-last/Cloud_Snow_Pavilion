package com.linlee.cloudsnow.common.config;

import com.baomidou.mybatisplus.annotation.DbType;
import com.baomidou.mybatisplus.extension.plugins.MybatisPlusInterceptor;
import com.baomidou.mybatisplus.extension.plugins.inner.PaginationInnerInterceptor;
import com.baomidou.mybatisplus.extension.plugins.inner.TenantLineInnerInterceptor;
import com.baomidou.mybatisplus.extension.plugins.handler.TenantLineHandler;
import com.linlee.cloudsnow.common.context.TenantContext;
import lombok.extern.slf4j.Slf4j;
import net.sf.jsqlparser.expression.Expression;
import net.sf.jsqlparser.expression.LongValue;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * MyBatis-Plus配置 - 多租户插件
 *
 * @author Cloud Snow Pavilion
 * @since 2025-11-22
 */
@Slf4j
@Configuration
public class MybatisPlusConfig {

    @Bean
    public MybatisPlusInterceptor mybatisPlusInterceptor() {
        MybatisPlusInterceptor interceptor = new MybatisPlusInterceptor();

        // 1. 添加租户插件（必须在分页插件之前）
        TenantLineInnerInterceptor tenantInterceptor = new TenantLineInnerInterceptor();
        tenantInterceptor.setTenantLineHandler(new TenantLineHandler() {
            @Override
            public Expression getTenantId() {
                // 从上下文获取租户ID
                Long tenantId = TenantContext.getTenantId();
                log.info("【租户拦截器】获取租户ID: {}, Thread: {}", tenantId, Thread.currentThread().getName());
                if (tenantId == null) {
                    log.error("【租户拦截器】租户ID为null！");
                    // 返回一个不存在的租户ID，让查询结果为空
                    return new LongValue(-999L);
                }
                return new LongValue(tenantId);
            }

            @Override
            public String getTenantIdColumn() {
                // 租户ID字段名
                return "tenant_id";
            }

            @Override
            public boolean ignoreTable(String tableName) {
                log.debug("【租户拦截器】检查表: {}, ignore: {}", tableName, TenantContext.isIgnore());
                // 1. 忽略租户表自身
                if ("tenant".equalsIgnoreCase(tableName)) {
                    return true;
                }
                // 2. 如果设置了忽略标记，则忽略所有表
                if (TenantContext.isIgnore()) {
                    return true;
                }
                return false;
            }
        });
        interceptor.addInnerInterceptor(tenantInterceptor);

        // 2. 添加分页插件（必须在租户插件之后）
        PaginationInnerInterceptor paginationInterceptor = new PaginationInnerInterceptor(DbType.MYSQL);
        paginationInterceptor.setMaxLimit(500L); // 单页最大数量限制
        interceptor.addInnerInterceptor(paginationInterceptor);
        
        return interceptor;
    }
}
