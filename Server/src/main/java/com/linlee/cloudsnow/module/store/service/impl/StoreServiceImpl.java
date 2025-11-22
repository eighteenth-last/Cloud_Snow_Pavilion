package com.linlee.cloudsnow.module.store.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.linlee.cloudsnow.module.store.entity.Store;
import com.linlee.cloudsnow.module.store.mapper.StoreMapper;
import com.linlee.cloudsnow.module.store.service.StoreService;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 门店Service实现
 *
 * @author Cloud Snow Pavilion
 * @since 2025-11-22
 */
@Service
public class StoreServiceImpl extends ServiceImpl<StoreMapper, Store> implements StoreService {

    @Override
    public List<Store> getNearbyStores(BigDecimal userLat, BigDecimal userLng, Double maxDistance) {
        // 查询所有营业中的门店
        List<Store> stores = this.list(new LambdaQueryWrapper<Store>()
                .eq(Store::getStatus, 1)
                .isNotNull(Store::getLat)
                .isNotNull(Store::getLng));

        // 计算距离并过滤
        return stores.stream()
                .filter(store -> {
                    double distance = calculateDistance(
                            userLat.doubleValue(),
                            userLng.doubleValue(),
                            store.getLat().doubleValue(),
                            store.getLng().doubleValue()
                    );
                    return distance <= maxDistance;
                })
                .sorted((s1, s2) -> {
                    // 按距离排序
                    double d1 = calculateDistance(
                            userLat.doubleValue(),
                            userLng.doubleValue(),
                            s1.getLat().doubleValue(),
                            s1.getLng().doubleValue()
                    );
                    double d2 = calculateDistance(
                            userLat.doubleValue(),
                            userLng.doubleValue(),
                            s2.getLat().doubleValue(),
                            s2.getLng().doubleValue()
                    );
                    return Double.compare(d1, d2);
                })
                .collect(Collectors.toList());
    }

    /**
     * 计算两点之间的距离（单位：公里）
     * 使用Haversine公式
     */
    private double calculateDistance(double lat1, double lng1, double lat2, double lng2) {
        final int EARTH_RADIUS = 6371; // 地球半径，单位：公里
        
        double latDistance = Math.toRadians(lat2 - lat1);
        double lngDistance = Math.toRadians(lng2 - lng1);
        
        double a = Math.sin(latDistance / 2) * Math.sin(latDistance / 2)
                + Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2))
                * Math.sin(lngDistance / 2) * Math.sin(lngDistance / 2);
        
        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
        
        return EARTH_RADIUS * c;
    }
}
