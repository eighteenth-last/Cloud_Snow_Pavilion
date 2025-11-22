package com.linlee.cloudsnow.module.staff.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 租户管理员实体（店铺老板账号-B端后台登录）
 * 
 * 说明：
 * - 此表存储租户（店铺老板）的登录账号
 * - 每个租户只有一个管理员账号
 * - 通过此账号登录后台，管理自己租户下的所有数据
 * - 员工、商品、订单等业务数据都通过tenant_id关联到租户
 *
 * @author Cloud Snow Pavilion
 * @since 2025-11-22
 */
@Data
@TableName("staff")
public class Staff implements Serializable {

    private static final long serialVersionUID = 1L;

    /** 员工ID */
    @TableId(value = "staff_id", type = IdType.AUTO)
    private Long staffId;

    /** 租户ID（店铺老板所属的租户） */
    private Long tenantId;

    /** 登录账号（后台管理系统登录用） */
    private String username;

    /** 密码（BCrypt加密） */
    private String password;

    /** 老板姓名 */
    private String name;

    /** 职位（如：创始人、老板） */
    private String position;

    /** 手机号 */
    private String phone;

    /** 状态：1在职 0离职 */
    private Integer status;

    /** 创建时间 */
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createdAt;
}
