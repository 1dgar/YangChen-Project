package com.agri.service.impl;

import com.agri.entity.News;
import com.agri.entity.Product;
import com.agri.entity.SupplyDemand;
import com.agri.entity.User;
import com.agri.mapper.NewsMapper;
import com.agri.mapper.ProductMapper;
import com.agri.mapper.SupplyDemandMapper;
import com.agri.mapper.UserMapper;
import com.agri.service.AdminService;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.*;

@Service
@RequiredArgsConstructor
public class AdminServiceImpl implements AdminService {

    private final UserMapper userMapper;
    private final ProductMapper productMapper;
    private final SupplyDemandMapper supplyDemandMapper;
    private final NewsMapper newsMapper;

    @Override
    public Page<User> getUsers(Integer page, Integer size, String keyword) {
        Page<User> pageParam = new Page<>(page, size);
        LambdaQueryWrapper<User> wrapper = new LambdaQueryWrapper<>();
        if (keyword != null && !keyword.isEmpty()) {
            wrapper.like(User::getUsername, keyword);
        }
        wrapper.orderByDesc(User::getCreateTime);
        return userMapper.selectPage(pageParam, wrapper);
    }

    @Override
    public boolean updateUserStatus(Long userId, Integer status) {
        User user = userMapper.selectById(userId);
        if (user == null) {
            return false;
        }
        user.setStatus(status);
        return userMapper.updateById(user) > 0;
    }

    @Override
    public Page<Product> getProducts(Integer page, Integer size) {
        Page<Product> pageParam = new Page<>(page, size);
        return productMapper.selectProductPage(pageParam);
    }

    @Override
    public boolean updateProductStatus(Long productId, Integer status) {
        Product product = productMapper.selectById(productId);
        if (product == null) {
            return false;
        }
        product.setStatus(status);
        return productMapper.updateById(product) > 0;
    }

    @Override
    public boolean addNews(News news) {
        return newsMapper.insert(news) > 0;
    }

    @Override
    public boolean updateNews(News news) {
        return newsMapper.updateById(news) > 0;
    }

    @Override
    public boolean deleteNews(Long id) {
        return newsMapper.deleteById(id) > 0;
    }

    @Override
    public Map<String, Object> getStatistics() {
        Map<String, Object> stats = new HashMap<>();
        stats.put("userCount", userMapper.selectCount(null));
        stats.put("productCount", productMapper.selectCount(null));
        stats.put("supplyDemandCount", supplyDemandMapper.selectCount(null));
        stats.put("newsCount", newsMapper.selectCount(null));
        return stats;
    }

    @Override
    public List<Map<String, Object>> getCategoryStats() {
        List<Map<String, Object>> result = new ArrayList<>();
        String[] colors = {"#4CAF50", "#FF9800", "#795548", "#F44336", "#2196F3"};
        String[] categories = {"蔬菜", "水果", "粮油", "畜牧", "水产品"};
        long maxCount = 1;
        
        List<Map<String, Object>> counts = productMapper.selectCategoryStats();
        Map<String, Long> countMap = new HashMap<>();
        for (Map<String, Object> item : counts) {
            String category = (String) item.get("category");
            Long count = ((Number) item.get("count")).longValue();
            countMap.put(category, count);
            if (count > maxCount) {
                maxCount = count;
            }
        }
        
        for (int i = 0; i < categories.length; i++) {
            Map<String, Object> item = new HashMap<>();
            item.put("name", categories[i]);
            long count = countMap.getOrDefault(categories[i], 0L);
            item.put("count", count);
            item.put("percent", maxCount > 0 ? (int) (count * 100.0 / maxCount) : 0);
            item.put("color", colors[i]);
            result.add(item);
        }
        return result;
    }

    @Override
    public List<Map<String, Object>> getRecentActivities() {
        List<Map<String, Object>> activities = new ArrayList<>();
        
        LambdaQueryWrapper<User> userWrapper = new LambdaQueryWrapper<>();
        userWrapper.orderByDesc(User::getCreateTime).last("LIMIT 2");
        List<User> recentUsers = userMapper.selectList(userWrapper);
        for (User user : recentUsers) {
            Map<String, Object> activity = new HashMap<>();
            activity.put("type", "user");
            activity.put("content", "新用户 " + user.getUsername() + " 注册成功");
            activity.put("time", formatTime(user.getCreateTime()));
            activities.add(activity);
        }
        
        LambdaQueryWrapper<Product> productWrapper = new LambdaQueryWrapper<>();
        productWrapper.orderByDesc(Product::getCreateTime).last("LIMIT 2");
        List<Product> recentProducts = productMapper.selectList(productWrapper);
        for (Product product : recentProducts) {
            Map<String, Object> activity = new HashMap<>();
            activity.put("type", "product");
            activity.put("content", "新产品 \"" + product.getName() + "\" 已发布");
            activity.put("time", formatTime(product.getCreateTime()));
            activities.add(activity);
        }
        
        LambdaQueryWrapper<News> newsWrapper = new LambdaQueryWrapper<>();
        newsWrapper.orderByDesc(News::getCreateTime).last("LIMIT 1");
        List<News> recentNews = newsMapper.selectList(newsWrapper);
        for (News news : recentNews) {
            Map<String, Object> activity = new HashMap<>();
            activity.put("type", "news");
            activity.put("content", "资讯 \"" + news.getTitle() + "\" 已发布");
            activity.put("time", formatTime(news.getCreateTime()));
            activities.add(activity);
        }
        
        activities.sort((a, b) -> {
            String timeA = (String) a.get("time");
            String timeB = (String) b.get("time");
            return timeB.compareTo(timeA);
        });
        
        return activities.subList(0, Math.min(activities.size(), 5));
    }
    
    private String formatTime(LocalDateTime time) {
        if (time == null) return "";
        long diff = java.time.Duration.between(time, LocalDateTime.now()).toMinutes();
        if (diff < 60) {
            return diff + "分钟前";
        } else if (diff < 1440) {
            return (diff / 60) + "小时前";
        } else {
            return (diff / 1440) + "天前";
        }
    }
}
