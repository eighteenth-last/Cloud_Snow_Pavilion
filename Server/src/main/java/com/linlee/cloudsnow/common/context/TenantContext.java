package com.linlee.cloudsnow.common.context;

import lombok.extern.slf4j.Slf4j;

/**
 * 租户上下文 - 线程本地变量存储当前租户ID
 *
 * @author Cloud Snow Pavilion
 * @since 2025-11-22
 */
@Slf4j
public class TenantContext {

    private static final ThreadLocal<Long> tenantIdHolder = new ThreadLocal<>();
    private static final ThreadLocal<Boolean> ignoreHolder = new ThreadLocal<>();

    /**
     * 设置当前租户ID
     */
    public static void setTenantId(Long tenantId) {
        log.info("【TenantContext.setTenantId】Thread: {}, 设置TenantId: {} -> {}", 
            Thread.currentThread().getName(), tenantIdHolder.get(), tenantId);
        tenantIdHolder.set(tenantId);
    }

    /**
     * 获取当前租户ID
     */
    public static Long getTenantId() {
        Long tenantId = tenantIdHolder.get();
        log.debug("【TenantContext.getTenantId】Thread: {}, TenantId: {}", 
            Thread.currentThread().getName(), tenantId);
        return tenantId;
    }
    
    /**
     * 设置是否忽略租户过滤
     */
    public static void setIgnore(Boolean ignore) {
        ignoreHolder.set(ignore);
    }
    
    /**
     * 是否忽略租户过滤
     */
    public static Boolean isIgnore() {
        return Boolean.TRUE.equals(ignoreHolder.get());
    }

    /**
     * 清除租户ID
     */
    public static void clear() {
        tenantIdHolder.remove();
        ignoreHolder.remove();
    }
}
