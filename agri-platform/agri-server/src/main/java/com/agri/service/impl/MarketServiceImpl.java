package com.agri.service.impl;

import com.agri.entity.MarketPrice;
import com.agri.entity.News;
import com.agri.mapper.MarketPriceMapper;
import com.agri.mapper.NewsMapper;
import com.agri.service.MarketService;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
@RequiredArgsConstructor
public class MarketServiceImpl extends ServiceImpl<MarketPriceMapper, MarketPrice> implements MarketService {

    private final MarketPriceMapper marketPriceMapper;
    private final NewsMapper newsMapper;

    @Override
    public List<MarketPrice> getPrices(String category) {
        LambdaQueryWrapper<MarketPrice> wrapper = new LambdaQueryWrapper<>();
        if (category != null && !category.isEmpty()) {
            wrapper.eq(MarketPrice::getCategory, category);
        }
        wrapper.orderByDesc(MarketPrice::getPriceDate);
        return list(wrapper);
    }

    @Override
    public Map<String, Object> getTrend(String category, Integer days) {
        List<MarketPrice> list = marketPriceMapper.selectTrend(category, days);
        
        List<String> dates = new ArrayList<>();
        List<Double> prices = new ArrayList<>();
        
        for (MarketPrice mp : list) {
            dates.add(mp.getPriceDate().toString());
            prices.add(mp.getPrice().doubleValue());
        }
        
        Map<String, Object> result = new HashMap<>();
        result.put("category", category);
        result.put("dates", dates);
        result.put("prices", prices);
        return result;
    }

    @Override
    public List<String> getCategories() {
        return Arrays.asList("蔬菜", "水果", "粮油", "畜牧", "水产", "茶叶", "中药材", "其他");
    }

    @Override
    public Page<News> getNews(Integer page, Integer size) {
        Page<News> pageParam = new Page<>(page, size);
        LambdaQueryWrapper<News> wrapper = new LambdaQueryWrapper<>();
        wrapper.orderByDesc(News::getCreateTime);
        return newsMapper.selectPage(pageParam, wrapper);
    }

    @Override
    public News getNewsDetail(Long id) {
        News news = newsMapper.selectById(id);
        if (news != null) {
            news.setViewCount(news.getViewCount() + 1);
            newsMapper.updateById(news);
        }
        return news;
    }
}
