package com.agri.service;

import com.agri.entity.Product;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;

public interface ProductService extends IService<Product> {
    
    Page<Product> getProductPage(Integer page, Integer size, String category, String keyword);
    
    Product getProductDetail(Long id);
    
    Page<Product> getMyProducts(Long userId, Integer page, Integer size);
    
    boolean publishProduct(Product product);
    
    boolean updateProduct(Product product, Long userId);
    
    boolean deleteProduct(Long id, Long userId);
}
