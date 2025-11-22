package com.linlee.cloudsnow.module.user.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.linlee.cloudsnow.module.user.entity.User;
import org.apache.ibatis.annotations.Mapper;

/**
 * 用户Mapper
 *
 * @author Cloud Snow Pavilion
 * @since 2025-11-22
 */
@Mapper
public interface UserMapper extends BaseMapper<User> {
}
