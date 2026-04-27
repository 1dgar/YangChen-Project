package com.agri.service;

import com.agri.entity.MarketPrice;
import com.agri.entity.News;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;
import java.util.Map;

public interface MarketService extends IService<MarketPrice> {
    List<MarketPrice> getPrices(String category);
    Map<String, Object> getTrend(String category, Integer days);
    List<String> getCategories();
    Page<News> getNews(Integer page, Integer size, String keyword, String sortBy);
    News getNewsDetail(Long id);
}
