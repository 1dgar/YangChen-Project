package com.agri.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@TableName("t_market_price")
public class MarketPrice {
    @TableId(type = IdType.AUTO)
    private Long id;
    
    private String category;
    private String productName;
    private BigDecimal price;
    private String unit;
    private String marketName;
    private LocalDate priceDate;
    private LocalDateTime createTime;
}
