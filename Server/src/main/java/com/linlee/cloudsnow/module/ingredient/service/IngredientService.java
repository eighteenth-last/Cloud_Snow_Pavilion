package com.linlee.cloudsnow.module.ingredient.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.linlee.cloudsnow.module.ingredient.entity.Ingredient;



/**
 * 原料Service
 *
 * @author Cloud Snow Pavilion
 * @since 2025-11-22
 */
public interface IngredientService extends IService<Ingredient> {

    /**
     * 入库
     *
     * @param ingredientId 原料ID
     * @param quantity 数量
     * @param operator 操作人
     * @param remark 备注
     */
    void stockIn(Long ingredientId, Integer quantity, String operator, String remark);

    /**
     * 出库
     *
     * @param ingredientId 原料ID
     * @param quantity 数量
     * @param operator 操作人
     * @param remark 备注
     */
    void stockOut(Long ingredientId, Integer quantity, String operator, String remark);
}
