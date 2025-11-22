package com.linlee.cloudsnow.module.tenant.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.linlee.cloudsnow.module.tenant.entity.Tenant;
import org.apache.ibatis.annotations.Mapper;

/**
 * 租户Mapper
 *
 * @author Cloud Snow Pavilion
 * @since 2025-11-22
 */
@Mapper
public interface TenantMapper extends BaseMapper<Tenant> {
}
