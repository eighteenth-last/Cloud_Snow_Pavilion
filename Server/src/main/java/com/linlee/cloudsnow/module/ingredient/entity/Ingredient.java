package com.linlee.cloudsnow.module.ingredient.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 原料实体
 *
 * @author Cloud Snow Pavilion
 * @since 2025-11-22
 */
@Data
@TableName("ingredient")
public class Ingredient implements Serializable {

    private static final long serialVersionUID = 1L;

    /** 原料ID */
    @TableId(value = "ingredient_id", type = IdType.AUTO)
    private Long ingredientId;

    /** 租户ID */
    private Long tenantId;

    /** 原料名称 */
    private String name;

    /** 分类 */
    private String category;

    /** 单位 */
    private String unit;

    /** 当前库存 */
    private Integer currentStock;

    /** 最小警戒库存 */
    private Integer minStock;

    /** 状态 */
    private Integer status;

    /** 创建时间 */
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createdAt;
}
