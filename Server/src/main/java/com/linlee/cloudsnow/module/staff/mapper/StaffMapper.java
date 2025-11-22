package com.linlee.cloudsnow.module.staff.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.linlee.cloudsnow.module.staff.entity.Staff;
import org.apache.ibatis.annotations.Mapper;

/**
 * 租户管理员Mapper（店铺老板）
 *
 * @author Cloud Snow Pavilion
 * @since 2025-11-22
 */
@Mapper
public interface StaffMapper extends BaseMapper<Staff> {
}
