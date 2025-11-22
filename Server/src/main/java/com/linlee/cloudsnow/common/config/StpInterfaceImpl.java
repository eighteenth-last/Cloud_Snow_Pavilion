package com.linlee.cloudsnow.common.config;

import cn.dev33.satoken.stp.StpInterface;
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

    /**
     * 返回指定账号id所拥有的权限码集合
     */
    @Override
    public List<String> getPermissionList(Object loginId, String loginType) {
        List<String> list = new ArrayList<>();
        // TODO: 从数据库查询用户权限
        // 这里先返回空列表，后续根据角色权限系统完善
        return list;
    }

    /**
     * 返回指定账号id所拥有的角色标识集合
     */
    @Override
    public List<String> getRoleList(Object loginId, String loginType) {
        List<String> list = new ArrayList<>();
        // TODO: 从数据库查询用户角色
        // 这里先返回空列表，后续根据角色权限系统完善
        return list;
    }
}
