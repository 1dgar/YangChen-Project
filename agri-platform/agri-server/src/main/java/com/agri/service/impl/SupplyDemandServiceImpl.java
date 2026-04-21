package com.agri.service.impl;

import com.agri.entity.SupplyDemand;
import com.agri.mapper.SupplyDemandMapper;
import com.agri.service.SupplyDemandService;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class SupplyDemandServiceImpl extends ServiceImpl<SupplyDemandMapper, SupplyDemand> implements SupplyDemandService {

    private final SupplyDemandMapper supplyDemandMapper;

    @Override
    public Page<SupplyDemand> getPage(Integer page, Integer size, String type, String category) {
        Page<SupplyDemand> pageParam = new Page<>(page, size);
        LambdaQueryWrapper<SupplyDemand> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(SupplyDemand::getStatus, 1)
               .orderByDesc(SupplyDemand::getCreateTime);
        
        if (type != null && !type.isEmpty()) {
            wrapper.eq(SupplyDemand::getType, type);
        }
        if (category != null && !category.isEmpty()) {
            wrapper.eq(SupplyDemand::getCategory, category);
        }
        
        return supplyDemandMapper.selectPage(pageParam, wrapper);
    }

    @Override
    public SupplyDemand getDetail(Long id) {
        return supplyDemandMapper.selectById(id);
    }

    @Override
    public Page<SupplyDemand> getByUserId(Long userId, Integer page, Integer size) {
        Page<SupplyDemand> pageParam = new Page<>(page, size);
        return supplyDemandMapper.selectByUserId(pageParam, userId);
    }

    @Override
    public boolean publish(SupplyDemand supplyDemand) {
        return save(supplyDemand);
    }

    @Override
    public boolean update(SupplyDemand supplyDemand, Long userId) {
        SupplyDemand exist = getById(supplyDemand.getId());
        if (exist == null || !exist.getUserId().equals(userId)) {
            return false;
        }
        return updateById(supplyDemand);
    }

    @Override
    public boolean delete(Long id, Long userId) {
        SupplyDemand exist = getById(id);
        if (exist == null || !exist.getUserId().equals(userId)) {
            return false;
        }
        return removeById(id);
    }
}
