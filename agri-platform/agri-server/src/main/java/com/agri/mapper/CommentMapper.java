package com.agri.mapper;

import com.agri.entity.Comment;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

@Mapper
public interface CommentMapper extends BaseMapper<Comment> {
    
    @Select("SELECT c.*, u.username, u.avatar FROM t_comment c " +
            "LEFT JOIN t_user u ON c.user_id = u.id " +
            "WHERE c.product_id = #{productId} AND c.deleted = 0 " +
            "ORDER BY c.create_time DESC")
    Page<Comment> selectByProductId(Page<Comment> page, @Param("productId") Long productId);
    
    @Select("SELECT c.*, u.username, u.avatar FROM t_comment c " +
            "LEFT JOIN t_user u ON c.user_id = u.id " +
            "WHERE c.user_id = #{userId} AND c.deleted = 0 " +
            "ORDER BY c.create_time DESC")
    Page<Comment> selectByUserId(Page<Comment> page, @Param("userId") Long userId);
}
