package com.agri.mapper;

import com.agri.entity.Product;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;
import java.util.Map;

@Mapper
public interface ProductMapper extends BaseMapper<Product> {
    
    @Select("SELECT p.*, u.username FROM t_product p LEFT JOIN t_user u ON p.user_id = u.id " +
            "WHERE p.deleted = 0 ORDER BY p.create_time DESC")
    Page<Product> selectProductPage(Page<Product> page);
    
    @Select("SELECT p.*, u.username FROM t_product p LEFT JOIN t_user u ON p.user_id = u.id " +
            "WHERE p.id = #{id} AND p.deleted = 0")
    Product selectProductById(Long id);
    
    @Select("SELECT p.*, u.username FROM t_product p LEFT JOIN t_user u ON p.user_id = u.id " +
            "WHERE p.user_id = #{userId} AND p.deleted = 0 ORDER BY p.create_time DESC")
    Page<Product> selectByUserId(Page<Product> page, @Param("userId") Long userId);
    
    @Select("SELECT category, COUNT(*) as count FROM t_product WHERE deleted = 0 GROUP BY category")
    List<Map<String, Object>> selectCategoryStats();
}
