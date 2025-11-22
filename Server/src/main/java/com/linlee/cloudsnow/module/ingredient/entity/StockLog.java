package com.linlee.cloudsnow.module.ingredient.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 库存日志实体
 *
 * @author Cloud Snow Pavilion
 * @since 2025-11-22
 */
@Data
@TableName("stock_log")
public class StockLog implements Serializable {

    private static final long serialVersionUID = 1L;

    /** 日志ID */
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    /** 租户ID */
    private Long tenantId;

    /** 原料ID */
    private Long ingredientId;

    /** 操作类型：in入库 / out出库 */
    private String type;

    /** 变动数量 */
    private Integer quantity;

    /** 操作人 */
    private String operator;

    /** 备注 */
    private String remark;

    /** 创建时间 */
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createdAt;
}
