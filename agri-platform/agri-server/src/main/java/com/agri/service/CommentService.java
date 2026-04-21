package com.agri.service;

import com.agri.entity.Comment;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;

public interface CommentService extends IService<Comment> {
    Page<Comment> getByProductId(Long productId, Integer page, Integer size);
    Page<Comment> getByUserId(Long userId, Integer page, Integer size);
    boolean add(Comment comment);
    boolean reply(Long commentId, String reply, Long userId);
}
