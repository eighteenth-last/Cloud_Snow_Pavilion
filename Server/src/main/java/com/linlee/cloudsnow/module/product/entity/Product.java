package com.linlee.cloudsnow.module.product.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 商品实体
 *
 * @author Cloud Snow Pavilion
 * @since 2025-11-22
 */
@Data
@TableName("product")
public class Product implements Serializable {

    private static final long serialVersionUID = 1L;

    /** 商品ID */
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    /** 租户ID */
    private Long tenantId;

    /** 商品名称 */
    private String name;

    /** 副标题 */
    private String subTitle;

    /** 售价 */
    private BigDecimal price;

    /** 原价 */
    private BigDecimal originPrice;

    /** 折扣 */
    private BigDecimal discount;

    /** 商品主图 */
    private String img;

    /** 总库存 */
    private Integer stock;

    /** 状态 1上架 0下架 */
    private Integer status;

    /** 分类ID */
    private Long categoryId;

    /** 创建时间 */
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createdAt;
}
