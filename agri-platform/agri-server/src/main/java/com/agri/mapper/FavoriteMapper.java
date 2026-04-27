package com.agri.mapper;

import com.agri.entity.Favorite;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Update;

@Mapper
public interface FavoriteMapper extends BaseMapper<Favorite> {
    
    @Select("SELECT f.* " +
            "FROM t_favorite f LEFT JOIN t_product p ON f.product_id = p.id " +
            "WHERE f.user_id = #{userId} AND f.deleted = 0 AND p.deleted = 0 " +
            "ORDER BY f.create_time DESC")
    Page<Favorite> selectByUserId(Page<Favorite> page, @Param("userId") Long userId);

    @Select("SELECT id, user_id, product_id, create_time, deleted " +
            "FROM t_favorite WHERE user_id = #{userId} AND product_id = #{productId}")
    Favorite selectAnyByUserIdAndProductId(@Param("userId") Long userId, @Param("productId") Long productId);

    @Update("UPDATE t_favorite SET deleted = 0, create_time = CURRENT_TIMESTAMP " +
            "WHERE id = #{id}")
    int reactivateById(@Param("id") Long id);
}
