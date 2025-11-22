package com.linlee.cloudsnow.module.product.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.linlee.cloudsnow.module.product.entity.Product;
import org.apache.ibatis.annotations.Mapper;

/**
 * 商品Mapper
 *
 * @author Cloud Snow Pavilion
 * @since 2025-11-22
 */
@Mapper
public interface ProductMapper extends BaseMapper<Product> {
}
