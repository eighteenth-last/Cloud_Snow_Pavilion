package com.linlee.cloudsnow.module.store.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.linlee.cloudsnow.module.store.entity.Store;

import java.math.BigDecimal;
import java.util.List;

/**
 * 门店Service
 *
 * @author Cloud Snow Pavilion
 * @since 2025-11-22
 */
public interface StoreService extends IService<Store> {

    /**
     * 根据距离查询附近门店
     *
     * @param lat 纬度
     * @param lng 经度
     * @param distance 距离（公里）
     * @return 门店列表
     */
    List<Store> getNearbyStores(BigDecimal lat, BigDecimal lng, Double distance);
}
