package com.linlee.cloudsnow.module.analysis.dto;

import lombok.Data;

import java.math.BigDecimal;

/**
 * 收入趋势项
 *
 * @author Cloud Snow Pavilion
 * @since 2025-12-17
 */
@Data
public class RevenueTrendItem {

    /** 日期（格式：yyyy-MM-dd） */
    private String date;

    /** 收入金额 */
    private BigDecimal revenue = BigDecimal.ZERO;

    /** 订单数 */
    private Integer orderCount = 0;
}
