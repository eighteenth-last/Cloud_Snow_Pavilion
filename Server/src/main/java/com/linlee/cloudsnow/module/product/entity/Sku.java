package com.linlee.cloudsnow.module.product.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 商品SKU实体
 *
 * @author Cloud Snow Pavilion
 * @since 2025-11-22
 */
@Data
@TableName("sku")
public class Sku implements Serializable {

    private static final long serialVersionUID = 1L;

    /** SKU ID */
    @TableId(value = "sku_id", type = IdType.AUTO)
    private Long skuId;

    /** 租户ID */
    private Long tenantId;

    /** 商品ID */
    private Long productId;

    /** 规格JSON */
    private String specJson;

    /** SKU价格 */
    private BigDecimal price;

    /** 库存 */
    private Integer stock;

    /** 创建时间 */
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createdAt;
}
