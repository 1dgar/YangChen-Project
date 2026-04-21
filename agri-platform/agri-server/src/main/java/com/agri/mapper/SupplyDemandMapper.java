package com.agri.mapper;

import com.agri.entity.SupplyDemand;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

@Mapper
public interface SupplyDemandMapper extends BaseMapper<SupplyDemand> {
    
    @Select("SELECT s.*, u.username FROM t_supply_demand s LEFT JOIN t_user u ON s.user_id = u.id " +
            "WHERE s.deleted = 0 ORDER BY s.create_time DESC")
    Page<SupplyDemand> selectPage(Page<SupplyDemand> page);
    
    @Select("SELECT s.*, u.username FROM t_supply_demand s LEFT JOIN t_user u ON s.user_id = u.id " +
            "WHERE s.id = #{id} AND s.deleted = 0")
    SupplyDemand selectById(Long id);
    
    @Select("SELECT s.*, u.username FROM t_supply_demand s LEFT JOIN t_user u ON s.user_id = u.id " +
            "WHERE s.user_id = #{userId} AND s.deleted = 0 ORDER BY s.create_time DESC")
    Page<SupplyDemand> selectByUserId(Page<SupplyDemand> page, @Param("userId") Long userId);
}
