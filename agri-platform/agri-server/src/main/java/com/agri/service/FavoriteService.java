package com.agri.service;

import com.agri.entity.Favorite;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;

public interface FavoriteService extends IService<Favorite> {
    Page<Favorite> getByUserId(Long userId, Integer page, Integer size);
    boolean add(Long userId, Long productId);
    boolean remove(Long userId, Long productId);
    boolean isFavorited(Long userId, Long productId);
}
