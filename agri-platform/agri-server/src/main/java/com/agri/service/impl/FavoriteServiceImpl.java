package com.agri.service.impl;

import com.agri.entity.Favorite;
import com.agri.entity.Product;
import com.agri.mapper.FavoriteMapper;
import com.agri.mapper.ProductMapper;
import com.agri.service.FavoriteService;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class FavoriteServiceImpl extends ServiceImpl<FavoriteMapper, Favorite> implements FavoriteService {

    private final FavoriteMapper favoriteMapper;
    private final ProductMapper productMapper;

    @Override
    public Page<Favorite> getByUserId(Long userId, Integer page, Integer size) {
        Page<Favorite> pageParam = new Page<>(page, size);
        Page<Favorite> favorites = favoriteMapper.selectByUserId(pageParam, userId);
        favorites.getRecords().forEach(favorite -> {
            Product product = productMapper.selectProductById(favorite.getProductId());
            favorite.setProduct(product);
        });
        return favorites;
    }

    @Override
    public boolean add(Long userId, Long productId) {
        Favorite existingFavorite = favoriteMapper.selectAnyByUserIdAndProductId(userId, productId);
        if (existingFavorite != null) {
            if (Integer.valueOf(0).equals(existingFavorite.getDeleted())) {
                return false;
            }
            return favoriteMapper.reactivateById(existingFavorite.getId()) > 0;
        }

        LambdaQueryWrapper<Favorite> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Favorite::getUserId, userId)
               .eq(Favorite::getProductId, productId);
        if (count(wrapper) > 0) {
            return false;
        }
        Favorite favorite = new Favorite();
        favorite.setUserId(userId);
        favorite.setProductId(productId);
        return save(favorite);
    }

    @Override
    public boolean remove(Long userId, Long productId) {
        LambdaQueryWrapper<Favorite> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Favorite::getUserId, userId)
               .eq(Favorite::getProductId, productId);
        return remove(wrapper);
    }

    @Override
    public boolean isFavorited(Long userId, Long productId) {
        LambdaQueryWrapper<Favorite> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Favorite::getUserId, userId)
               .eq(Favorite::getProductId, productId);
        return count(wrapper) > 0;
    }
}
