package com.linlee.cloudsnow.module.analysis.dto;

import lombok.Data;

import java.math.BigDecimal;

/**
 * 收入统计DTO
 *
 * @author Cloud Snow Pavilion
 * @since 2025-11-22
 */
@Data
public class RevenueStatistics {

    /** 订单总数 */
    private Integer orderCount = 0;

    /** 总收入 */
    private BigDecimal totalRevenue = BigDecimal.ZERO;

    /** 平均订单金额 */
    private BigDecimal avgOrderAmount = BigDecimal.ZERO;
}
