package com.linlee.cloudsnow.common.config;

import cn.dev33.satoken.stp.StpInterface;
import cn.dev33.satoken.stp.StpUtil;
import com.linlee.cloudsnow.module.staff.entity.Staff;
import com.linlee.cloudsnow.module.staff.service.StaffService;
import com.linlee.cloudsnow.module.user.entity.User;
import com.linlee.cloudsnow.module.user.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

/**
 * Sa-Token权限验证接口实现
 *
 * @author Cloud Snow Pavilion
 * @since 2025-11-22
 */
@Component
public class StpInterfaceImpl implements StpInterface {

    @Autowired
    private StaffService staffService;
    
    @Autowired
    private UserService userService;

    /**
     * 返回指定账号id所拥有的权限码集合
     */
    @Override
    public List<String> getPermissionList(Object loginId, String loginType) {
        List<String> permissions = new ArrayList<>();
        
        // 根据登录类型返回不同的权限
        if ("admin".equals(loginType)) {
            // 商家后台管理员权限
            permissions.add("admin:manage");
            permissions.add("product:manage");
            permissions.add("order:manage");
            permissions.add("staff:manage");
            permissions.add("store:manage");
            permissions.add("coupon:manage");
            permissions.add("ingredient:manage");
            permissions.add("analysis:view");
        } else {
            // C端用户权限
            permissions.add("user:order");
            permissions.add("user:address");
            permissions.add("user:coupon");
        }
        
        return permissions;
    }

    /**
     * 返回指定账号id所拥有的角色标识集合
     */
    @Override
    public List<String> getRoleList(Object loginId, String loginType) {
        List<String> roles = new ArrayList<>();
        
        try {
            Long userId = Long.parseLong(loginId.toString());
            
            // 根据登录类型查询角色
            if ("admin".equals(loginType)) {
                // 查询商家信息
                Staff staff = staffService.getById(userId);
                if (staff != null) {
                    // 根据职位判断角色
                    if ("老板".equals(staff.getPosition()) || "创始人".equals(staff.getPosition())) {
                        roles.add("admin");
                        roles.add("owner");
                    } else {
                        roles.add("admin");
                        roles.add("staff");
                    }
                }
            } else {
                // 查询用户信息
                User user = userService.getById(userId);
                if (user != null) {
                    roles.add("user");
                    // 根据会员等级添加角色
                    if (user.getVipLevel() != null && user.getVipLevel() > 0) {
                        roles.add("vip");
                    }
                }
            }
        } catch (Exception e) {
            // 角色查询异常，返回空列表
            System.err.println("查询用户角色失败: " + e.getMessage());
        }
        
        return roles;
    }
}
