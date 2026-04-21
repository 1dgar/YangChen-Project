package com.agri.mapper;

import com.agri.entity.Favorite;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

@Mapper
public interface FavoriteMapper extends BaseMapper<Favorite> {
    
    @Select("SELECT f.*, p.name as product_name, p.image as product_image, p.price, p.unit " +
            "FROM t_favorite f LEFT JOIN t_product p ON f.product_id = p.id " +
            "WHERE f.user_id = #{userId} AND f.deleted = 0 AND p.deleted = 0 " +
            "ORDER BY f.create_time DESC")
    Page<Favorite> selectByUserId(Page<Favorite> page, @Param("userId") Long userId);
}
