package com.linlee.cloudsnow.module.tenant.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 租户实体
 *
 * @author Cloud Snow Pavilion
 * @since 2025-11-22
 */
@Data
@TableName("tenant")
public class Tenant implements Serializable {

    private static final long serialVersionUID = 1L;

    /** 租户ID */
    @TableId(value = "tenant_id", type = IdType.AUTO)
    private Long tenantId;

    /** 租户名称 */
    private String name;

    /** 管理员用户ID */
    private Long adminUserId;

    /** 状态：1正常 0禁用 */
    private Integer status;

    /** 创建时间 */
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createdAt;

    /** 更新时间 */
    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updatedAt;
}
