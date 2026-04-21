package com.agri.mapper;

import com.agri.entity.MarketPrice;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface MarketPriceMapper extends BaseMapper<MarketPrice> {
    
    @Select("SELECT * FROM t_market_price WHERE category = #{category} " +
            "AND price_date >= DATEADD('DAY', -#{days}, CURRENT_DATE) " +
            "ORDER BY price_date ASC")
    List<MarketPrice> selectTrend(@Param("category") String category, @Param("days") int days);
}
