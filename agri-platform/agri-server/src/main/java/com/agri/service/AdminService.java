package com.agri.service;

import com.agri.entity.News;
import com.agri.entity.Product;
import com.agri.entity.User;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;

import java.util.List;
import java.util.Map;

public interface AdminService {
    Page<User> getUsers(Integer page, Integer size, String keyword);
    boolean updateUserStatus(Long userId, Integer status);
    Page<Product> getProducts(Integer page, Integer size);
    boolean updateProductStatus(Long productId, Integer status);
    boolean addNews(News news);
    boolean updateNews(News news);
    boolean deleteNews(Long id);
    Map<String, Object> getStatistics();
    List<Map<String, Object>> getCategoryStats();
    List<Map<String, Object>> getRecentActivities();
}
