package com.agri.controller;

import com.agri.annotation.RequireLogin;
import com.agri.entity.Favorite;
import com.agri.service.FavoriteService;
import com.agri.utils.UserContext;
import com.agri.vo.Result;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/favorite")
@RequiredArgsConstructor
public class FavoriteController {

    private final FavoriteService favoriteService;

    @GetMapping("/my/{userId}")
    @RequireLogin
    public Result<Page<Favorite>> myFavorites(
            @PathVariable Long userId,
            @RequestParam(defaultValue = "1") Integer page,
            @RequestParam(defaultValue = "10") Integer size) {
        Long currentUserId = UserContext.getCurrentUserId();
        if (!userId.equals(currentUserId)) {
            return Result.error("无权查看");
        }
        return Result.success(favoriteService.getByUserId(userId, page, size));
    }

    @PostMapping
    @RequireLogin
    public Result<?> add(@RequestBody Map<String, Long> params) {
        Long userId = UserContext.getCurrentUserId();
        Long productId = params.get("productId");
        if (favoriteService.add(userId, productId)) {
            return Result.success("收藏成功");
        }
        return Result.error("收藏失败或已收藏");
    }

    @DeleteMapping("/{productId}")
    @RequireLogin
    public Result<?> remove(@PathVariable Long productId) {
        Long userId = UserContext.getCurrentUserId();
        if (favoriteService.remove(userId, productId)) {
            return Result.success("取消收藏成功");
        }
        return Result.error("取消收藏失败");
    }

    @GetMapping("/check/{productId}")
    @RequireLogin
    public Result<Boolean> check(@PathVariable Long productId) {
        Long userId = UserContext.getCurrentUserId();
        return Result.success(favoriteService.isFavorited(userId, productId));
    }
}
