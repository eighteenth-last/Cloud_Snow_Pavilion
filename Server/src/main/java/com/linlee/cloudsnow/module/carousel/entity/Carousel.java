package com.linlee.cloudsnow.module.carousel.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 轮播图实体
 *
 * @author Cloud Snow Pavilion
 * @since 2025-12-17
 */
@Data
@TableName("carousel")
public class Carousel implements Serializable {

    private static final long serialVersionUID = 1L;

    /** 轮播图ID */
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    /** 租户ID */
    private Long tenantId;

    /** 标题 */
    private String title;

    /** 图片地址 */
    private String imgUrl;

    /** 跳转类型：0-不跳转 1-商品详情 2-页面路径 3-外部链接 */
    private Integer jumpType;

    /** 跳转值（商品ID/页面路径/外部链接） */
    private String jumpVal;

    /** 排序值，数值越小越靠前 */
    private Integer sort;

    /** 状态：0-禁用 1-启用 */
    private Integer status;

    /** 创建时间 */
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;

    /** 更新时间 */
    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updateTime;
}
