package com.linlee.cloudsnow.module.store.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;

/**
 * 门店实体
 *
 * @author Cloud Snow Pavilion
 * @since 2025-11-22
 */
@Data
@TableName("store")
public class Store implements Serializable {

    private static final long serialVersionUID = 1L;

    /** 门店ID */
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    /** 租户ID */
    private Long tenantId;

    /** 门店名称 */
    private String name;

    /** 地址 */
    private String address;

    /** 纬度 */
    private BigDecimal lat;

    /** 经度 */
    private BigDecimal lng;

    /** 营业时间 */
    private String businessHours;

    /** 电话 */
    private String phone;

    /** 状态：1营业 0停业 */
    private Integer status;
}
