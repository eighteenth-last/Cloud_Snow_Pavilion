package com.linlee.cloudsnow.common.util;

import cn.dev33.satoken.stp.StpUtil;

/**
 * 登录用户工具类
 *
 * @author Cloud Snow Pavilion
 * @since 2025-12-17
 */
public class LoginUserUtil {

    /**
     * 获取当前登录的C端用户ID
     * 如果是B端管理员登录，返回null
     */
    public static Long getCurrentUserId() {
        if (!StpUtil.isLogin()) {
            return null;
        }
        
        Object loginId = StpUtil.getLoginId();
        String loginIdStr = String.valueOf(loginId);
        
        // B端管理员格式：admin:staffId
        if (loginIdStr.startsWith("admin:")) {
            return null;
        }
        
        // C端用户格式：直接是userId
        try {
            return Long.parseLong(loginIdStr);
        } catch (NumberFormatException e) {
            return null;
        }
    }
    
    /**
     * 获取当前登录的B端管理员ID（staff_id）
     * 如果是C端用户登录，返回null
     */
    public static Long getCurrentStaffId() {
        if (!StpUtil.isLogin()) {
            return null;
        }
        
        Object loginId = StpUtil.getLoginId();
        String loginIdStr = String.valueOf(loginId);
        
        // B端管理员格式：admin:staffId
        if (loginIdStr.startsWith("admin:")) {
            String staffIdStr = loginIdStr.substring(6);
            try {
                return Long.parseLong(staffIdStr);
            } catch (NumberFormatException e) {
                return null;
            }
        }
        
        return null;
    }
    
    /**
     * 判断当前登录用户是否为C端用户
     */
    public static boolean isCUser() {
        return getCurrentUserId() != null;
    }
    
    /**
     * 判断当前登录用户是否为B端管理员
     */
    public static boolean isAdmin() {
        return getCurrentStaffId() != null;
    }
}
