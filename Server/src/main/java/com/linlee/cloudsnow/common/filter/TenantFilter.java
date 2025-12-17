package com.linlee.cloudsnow.common.filter;

import cn.dev33.satoken.stp.StpUtil;
import com.linlee.cloudsnow.common.context.TenantContext;
import com.linlee.cloudsnow.module.staff.entity.Staff;
import com.linlee.cloudsnow.module.staff.service.StaffService;
import com.linlee.cloudsnow.module.user.entity.User;
import com.linlee.cloudsnow.module.user.service.UserService;
import jakarta.servlet.*;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 租户过滤器 - 从当前登录用户获取租户ID并设置到上下文
 *
 * @author Cloud Snow Pavilion
 * @since 2025-11-22
 */
@Slf4j
@Component
@Order(1) // 确保在其他过滤器之前执行
public class TenantFilter implements Filter {

    @Autowired
    private UserService userService;
    
    @Autowired
    private StaffService staffService;
    
    // 简单的内存缓存，避免每次请求都查询数据库
    private final Map<Long, Long> userTenantCache = new ConcurrentHashMap<>();

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {
        
        HttpServletRequest httpRequest = (HttpServletRequest) request;
        String requestURI = httpRequest.getRequestURI();
        
        log.info("【TenantFilter】请求开始: {}, 当前TenantContext: {}", requestURI, TenantContext.getTenantId());
        
        try {
            // 对于需要登录的接口，从当前登录用户获取租户ID
            if (StpUtil.isLogin()) {
                Object loginId = StpUtil.getLoginId();
                String loginIdStr = String.valueOf(loginId);
                
                Long tenantId = null;
                
                // 区分B端（后台管理-租户/老板）和C端（小程序用户）
                if (loginIdStr.startsWith("admin:")) {
                    // B端：租户/老板登录
                    String staffIdStr = loginIdStr.substring(6);
                    if (staffIdStr.isEmpty()) {
                        log.error("请求 {} - loginId格式错误: {}", requestURI, loginIdStr);
                        throw new RuntimeException("登录ID格式错误");
                    }
                    Long staffId = Long.parseLong(staffIdStr);
                    tenantId = userTenantCache.get(staffId);
                    
                    if (tenantId == null) {
                        // 临时设置一个标记，让拦截器跳过
                        TenantContext.setIgnore(true);
                        try {
                            Staff staff = staffService.getById(staffId);
                            if (staff != null && staff.getTenantId() != null) {
                                tenantId = staff.getTenantId();
                                userTenantCache.put(staffId, tenantId);
                                log.debug("请求 {} - 设置租户ID: {} (从数据库-老板账号)", requestURI, tenantId);
                            } else {
                                log.error("请求 {} - 老板账号 {} 没有租户ID，数据异常", requestURI, staffId);
                                throw new RuntimeException("租户数据异常，请联系管理员");
                            }
                        } finally {
                            // 清除忽略标记
                            TenantContext.setIgnore(false);
                        }
                    } else {
                        log.debug("请求 {} - 设置租户ID: {} (从缓存-老板账号)", requestURI, tenantId);
                    }
                } else {
                    // C端：小程序C端用户登录
                    try {
                        Long userId = Long.parseLong(loginIdStr);
                        
                        tenantId = userTenantCache.get(userId);
                        
                        if (tenantId == null) {
                            // 临时设置一个标记，让拦截器跳过
                            TenantContext.setIgnore(true);
                            try {
                                User user = userService.getById(userId);
                                if (user != null && user.getTenantId() != null) {
                                    tenantId = user.getTenantId();
                                    userTenantCache.put(userId, tenantId);
                                    log.debug("请求 {} - 设置租户ID: {} (从数据库-C端用户)", requestURI, tenantId);
                                } else {
                                    log.debug("请求 {} - C端用户 {} 没有租户ID", requestURI, userId);
                                    // C端用户可以没有租户ID，不抛出异常
                                }
                            } finally {
                                // 清除忽略标记
                                TenantContext.setIgnore(false);
                            }
                        } else {
                            log.debug("请求 {} - 设置租户ID: {} (从缓存-C端用户)", requestURI, tenantId);
                        }
                    } catch (NumberFormatException e) {
                        log.error("请求 {} - loginId格式错误: {}", requestURI, loginIdStr);
                        throw new RuntimeException("登录ID格式错误");
                    }
                }
                
                // 设置租户ID到上下文（这个必须在 setIgnore(false) 之后）
                if (tenantId != null) {
                    TenantContext.setTenantId(tenantId);
                    log.info("【TenantFilter】请求 {} - 最终设置租户ID: {}, 验证: {}", requestURI, tenantId, TenantContext.getTenantId());
                }
            } else {
                // 未登录的请求，不设置租户ID
                log.debug("请求 {} - 未登录，不设置租户ID", requestURI);
            }
            
            // 继续处理请求
            log.info("【TenantFilter】请求 {} - chain.doFilter前 TenantContext: {}", requestURI, TenantContext.getTenantId());
            chain.doFilter(request, response);
            log.info("【TenantFilter】请求 {} - chain.doFilter后 TenantContext: {}", requestURI, TenantContext.getTenantId());
            
        } finally {
            // 请求结束后清除租户上下文，避免线程复用时的数据污染
            log.info("【TenantFilter】请求 {} - 清除 TenantContext", requestURI);
            TenantContext.clear();
        }
    }
    
    /**
     * 清除缓存（当用户租户发生变化时调用）
     */
    public void clearCache(Long userId) {
        userTenantCache.remove(userId);
        log.info("清除用户 {} 的租户缓存", userId);
    }
    
    /**
     * 清空所有缓存
     */
    public void clearAllCache() {
        userTenantCache.clear();
        log.info("清空所有租户缓存");
    }
}
