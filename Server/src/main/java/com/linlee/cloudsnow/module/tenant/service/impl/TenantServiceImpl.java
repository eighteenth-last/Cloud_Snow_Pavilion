package com.linlee.cloudsnow.module.tenant.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.linlee.cloudsnow.module.tenant.entity.Tenant;
import com.linlee.cloudsnow.module.tenant.mapper.TenantMapper;
import com.linlee.cloudsnow.module.tenant.service.TenantService;
import org.springframework.stereotype.Service;

/**
 * 租户Service实现
 *
 * @author Cloud Snow Pavilion
 * @since 2025-11-22
 */
@Service
public class TenantServiceImpl extends ServiceImpl<TenantMapper, Tenant> implements TenantService {
}
