package com.agri.controller;

import com.agri.annotation.RequireAdmin;
import com.agri.entity.News;
import com.agri.entity.Product;
import com.agri.entity.User;
import com.agri.service.AdminService;
import com.agri.vo.Result;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/admin")
@RequiredArgsConstructor
@RequireAdmin
public class AdminController {

    private final AdminService adminService;

    @GetMapping("/users")
    public Result<Page<User>> users(
            @RequestParam(defaultValue = "1") Integer page,
            @RequestParam(defaultValue = "10") Integer size,
            @RequestParam(required = false) String keyword) {
        return Result.success(adminService.getUsers(page, size, keyword));
    }

    @PutMapping("/user/{id}/status")
    public Result<?> updateUserStatus(@PathVariable Long id, @RequestBody Map<String, Integer> params) {
        if (adminService.updateUserStatus(id, params.get("status"))) {
            return Result.success("操作成功");
        }
        return Result.error("操作失败");
    }

    @GetMapping("/products")
    public Result<Page<Product>> products(
            @RequestParam(defaultValue = "1") Integer page,
            @RequestParam(defaultValue = "10") Integer size) {
        return Result.success(adminService.getProducts(page, size));
    }

    @PutMapping("/product/{id}/status")
    public Result<?> updateProductStatus(@PathVariable Long id, @RequestBody Map<String, Integer> params) {
        if (adminService.updateProductStatus(id, params.get("status"))) {
            return Result.success("操作成功");
        }
        return Result.error("操作失败");
    }

    @PostMapping("/news")
    public Result<?> addNews(@RequestBody News news) {
        if (adminService.addNews(news)) {
            return Result.success("发布成功");
        }
        return Result.error("发布失败");
    }

    @PutMapping("/news/{id}")
    public Result<?> updateNews(@PathVariable Long id, @RequestBody News news) {
        news.setId(id);
        if (adminService.updateNews(news)) {
            return Result.success("更新成功");
        }
        return Result.error("更新失败");
    }

    @DeleteMapping("/news/{id}")
    public Result<?> deleteNews(@PathVariable Long id) {
        if (adminService.deleteNews(id)) {
            return Result.success("删除成功");
        }
        return Result.error("删除失败");
    }

    @GetMapping("/statistics")
    public Result<Map<String, Object>> statistics() {
        return Result.success(adminService.getStatistics());
    }
    
    @GetMapping("/category-stats")
    public Result<List<Map<String, Object>>> categoryStats() {
        return Result.success(adminService.getCategoryStats());
    }
    
    @GetMapping("/recent-activities")
    public Result<List<Map<String, Object>>> recentActivities() {
        return Result.success(adminService.getRecentActivities());
    }
}
