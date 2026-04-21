package com.agri.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@TableName("t_supply_demand")
public class SupplyDemand {
    @TableId(type = IdType.AUTO)
    private Long id;
    
    private Long userId;
    private String type;
    private String title;
    private String category;
    private String quantity;
    private String price;
    private String content;
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
