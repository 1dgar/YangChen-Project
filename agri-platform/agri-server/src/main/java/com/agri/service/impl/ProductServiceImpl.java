package com.agri.service.impl;

import com.agri.entity.Product;
import com.agri.mapper.ProductMapper;
import com.agri.service.ProductService;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.RequiredArgsConstructor;
import java.util.Objects;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ProductServiceImpl extends ServiceImpl<ProductMapper, Product> implements ProductService {

    private final ProductMapper productMapper;

    @Override
    public Page<Product> getProductPage(Integer page, Integer size, String category, String keyword) {
        Page<Product> pageParam = new Page<>(page, size);
        LambdaQueryWrapper<Product> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Product::getStatus, 1)
               .orderByDesc(Product::getCreateTime);
        
        if (category != null && !category.isEmpty()) {
            wrapper.eq(Product::getCategory, category);
        }
        if (keyword != null && !keyword.isEmpty()) {
            wrapper.like(Product::getName, keyword);
        }
        
        return productMapper.selectPage(pageParam, wrapper);
    }

    @Override
    public Product getProductDetail(Long id) {
        return productMapper.selectProductById(id);
    }

    @Override
    public Page<Product> getMyProducts(Long userId, Integer page, Integer size) {
        Page<Product> pageParam = new Page<>(page, size);
        return productMapper.selectByUserId(pageParam, userId);
    }

    @Override
    public boolean publishProduct(Product product) {
        return save(product);
    }

    @Override
    public boolean updateProduct(Product product, Long userId) {
        Product exist = getById(product.getId());
        if (exist == null || !exist.getUserId().equals(userId)) {
            return false;
        }
        return updateById(product);
    }

    @Override
    public boolean deleteProduct(Long id, Long userId) {
        Product exist = getById(id);
        if (exist == null || !exist.getUserId().equals(userId)) {
            return false;
        }
        return removeById(id);
    }
}
