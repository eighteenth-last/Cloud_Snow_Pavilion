package com.linlee.cloudsnow.module.product.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.linlee.cloudsnow.module.product.entity.Product;
import com.linlee.cloudsnow.module.product.mapper.ProductMapper;
import com.linlee.cloudsnow.module.product.service.ProductService;
import org.springframework.stereotype.Service;

/**
 * 商品Service实现
 *
 * @author Cloud Snow Pavilion
 * @since 2025-11-22
 */
@Service
public class ProductServiceImpl extends ServiceImpl<ProductMapper, Product> implements ProductService {
}
