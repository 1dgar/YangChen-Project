package com.agri.controller;

import com.agri.entity.MarketPrice;
import com.agri.entity.News;
import com.agri.service.MarketService;
import com.agri.service.ProductService;
import com.agri.service.SupplyDemandService;
import com.agri.vo.Result;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;

@RestController
@RequestMapping("/api/market")
@RequiredArgsConstructor
public class MarketController {

    private final MarketService marketService;
    private final ProductService productService;
    private final SupplyDemandService supplyDemandService;

    @GetMapping("/price")
    public Result<List<MarketPrice>> price(@RequestParam(required = false) String category) {
        return Result.success(marketService.getPrices(category));
    }

    @GetMapping("/trend/{category}")
    public Result<Map<String, Object>> trend(@PathVariable String category,
                                             @RequestParam(defaultValue = "30") Integer days) {
        return Result.success(marketService.getTrend(category, days));
    }

    @GetMapping("/categories")
    public Result<List<String>> categories() {
        return Result.success(marketService.getCategories());
    }

    @GetMapping("/home-stats")
    public Result<Map<String, Object>> homeStats() {
        Map<String, Object> stats = new HashMap<>();
        
        LocalDateTime todayStart = LocalDate.now().atStartOfDay();
        
        QueryWrapper<com.agri.entity.SupplyDemand> demandWrapper = new QueryWrapper<>();
        demandWrapper.eq("type", "demand");
        demandWrapper.ge("create_time", todayStart);
        long todayDemandCount = supplyDemandService.count(demandWrapper);
        stats.put("todayDemand", todayDemandCount);
        
        Random random = new Random();
        double priceChange = (random.nextDouble() * 6 - 2);
        stats.put("priceChange", Math.round(priceChange * 10) / 10.0);
        stats.put("priceTrend", priceChange >= 0 ? "up" : "down");
        
        return Result.success(stats);
    }
}
