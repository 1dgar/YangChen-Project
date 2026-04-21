package com.agri.controller;

import com.agri.entity.News;
import com.agri.service.MarketService;
import com.agri.vo.Result;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/news")
@RequiredArgsConstructor
public class NewsController {

    private final MarketService marketService;

    @GetMapping("/list")
    public Result<Page<News>> list(
            @RequestParam(defaultValue = "1") Integer page,
            @RequestParam(defaultValue = "10") Integer size) {
        return Result.success(marketService.getNews(page, size));
    }

    @GetMapping("/{id}")
    public Result<News> detail(@PathVariable Long id) {
        News news = marketService.getNewsDetail(id);
        if (news != null) {
            return Result.success(news);
        }
        return Result.error("资讯不存在");
    }
}
