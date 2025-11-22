package com.linlee.cloudsnow.module.product.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.linlee.cloudsnow.module.product.entity.Category;
import com.linlee.cloudsnow.module.product.mapper.CategoryMapper;
import com.linlee.cloudsnow.module.product.service.CategoryService;
import org.springframework.stereotype.Service;

/**
 * 分类Service实现
 *
 * @author Cloud Snow Pavilion
 * @since 2025-11-22
 */
@Service
public class CategoryServiceImpl extends ServiceImpl<CategoryMapper, Category> implements CategoryService {
}
