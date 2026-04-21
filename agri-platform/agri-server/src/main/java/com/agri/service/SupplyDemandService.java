package com.agri.service;

import com.agri.entity.SupplyDemand;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;

public interface SupplyDemandService extends IService<SupplyDemand> {
    Page<SupplyDemand> getPage(Integer page, Integer size, String type, String category);
    SupplyDemand getDetail(Long id);
    Page<SupplyDemand> getByUserId(Long userId, Integer page, Integer size);
    boolean publish(SupplyDemand supplyDemand);
    boolean update(SupplyDemand supplyDemand, Long userId);
    boolean delete(Long id, Long userId);
}
