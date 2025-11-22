package com.linlee.cloudsnow.module.analysis.dto;

import lombok.Data;

import java.math.BigDecimal;

/**
 * 商品销量统计DTO
 *
 * @author Cloud Snow Pavilion
 * @since 2025-11-22
 */
@Data
public class ProductSalesStatistics {

    /** SKU ID */
    private Long skuId;

    /** 销售数量 */
    private Integer salesCount = 0;

    /** 销售金额 */
    private BigDecimal salesAmount = BigDecimal.ZERO;
}
