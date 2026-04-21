package com.agri.service.impl;

import com.agri.entity.Comment;
import com.agri.entity.Product;
import com.agri.mapper.CommentMapper;
import com.agri.mapper.ProductMapper;
import com.agri.service.CommentService;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class CommentServiceImpl extends ServiceImpl<CommentMapper, Comment> implements CommentService {

    private final CommentMapper commentMapper;
    private final ProductMapper productMapper;

    @Override
    public Page<Comment> getByProductId(Long productId, Integer page, Integer size) {
        Page<Comment> pageParam = new Page<>(page, size);
        return commentMapper.selectByProductId(pageParam, productId);
    }

    @Override
    public Page<Comment> getByUserId(Long userId, Integer page, Integer size) {
        Page<Comment> pageParam = new Page<>(page, size);
        return commentMapper.selectByUserId(pageParam, userId);
    }

    @Override
    public boolean add(Comment comment) {
        return save(comment);
    }

    @Override
    public boolean reply(Long commentId, String reply, Long userId) {
        Comment comment = getById(commentId);
        if (comment == null) {
            return false;
        }
        Product product = productMapper.selectById(comment.getProductId());
        if (product == null || !product.getUserId().equals(userId)) {
            return false;
        }
        comment.setReply(reply);
        comment.setReplyTime(LocalDateTime.now());
        return updateById(comment);
    }
}
