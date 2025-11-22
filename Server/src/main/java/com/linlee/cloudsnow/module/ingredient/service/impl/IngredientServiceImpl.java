package com.linlee.cloudsnow.module.ingredient.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.linlee.cloudsnow.common.context.TenantContext;
import com.linlee.cloudsnow.common.exception.BusinessException;
import com.linlee.cloudsnow.module.ingredient.entity.Ingredient;
import com.linlee.cloudsnow.module.ingredient.entity.StockLog;
import com.linlee.cloudsnow.module.ingredient.mapper.IngredientMapper;
import com.linlee.cloudsnow.module.ingredient.mapper.StockLogMapper;
import com.linlee.cloudsnow.module.ingredient.service.IngredientService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;



/**
 * 原料Service实现
 *
 * @author Cloud Snow Pavilion
 * @since 2025-11-22
 */
@Service
public class IngredientServiceImpl extends ServiceImpl<IngredientMapper, Ingredient> implements IngredientService {

    @Autowired
    private StockLogMapper stockLogMapper;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void stockIn(Long ingredientId, Integer quantity, String operator, String remark) {
        Ingredient ingredient = this.getById(ingredientId);
        if (ingredient == null) {
            throw new BusinessException("原料不存在");
        }

        // 增加库存
        ingredient.setCurrentStock(ingredient.getCurrentStock() + quantity);
        this.updateById(ingredient);

        // 记录日志
        StockLog log = new StockLog();
        log.setTenantId(TenantContext.getTenantId());
        log.setIngredientId(ingredientId);
        log.setType("in");
        log.setQuantity(quantity);
        log.setOperator(operator);
        log.setRemark(remark);
        stockLogMapper.insert(log);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void stockOut(Long ingredientId, Integer quantity, String operator, String remark) {
        Ingredient ingredient = this.getById(ingredientId);
        if (ingredient == null) {
            throw new BusinessException("原料不存在");
        }
        if (ingredient.getCurrentStock() < quantity) {
            throw new BusinessException("库存不足");
        }

        // 减少库存
        ingredient.setCurrentStock(ingredient.getCurrentStock() - quantity);
        this.updateById(ingredient);

        // 记录日志
        StockLog log = new StockLog();
        log.setTenantId(TenantContext.getTenantId());
        log.setIngredientId(ingredientId);
        log.setType("out");
        log.setQuantity(quantity);
        log.setOperator(operator);
        log.setRemark(remark);
        stockLogMapper.insert(log);
    }
}
