package com.agri.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@TableName("t_product")
public class Product {
    @TableId(type = IdType.AUTO)
    private Long id;
    
    private Long userId;
    private String name;
    private String category;
    private BigDecimal price;
    private String unit;
    private Integer stock;
    private String image;
    private String description;
    private String contactName;
    private String contactPhone;
    private String contactAddress;
    private Integer status;
    
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;
    
    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updateTime;
    
    @TableLogic
    @TableField(fill = FieldFill.INSERT)
    private Integer deleted;
    
    @TableField(exist = false)
    private String username;
}
