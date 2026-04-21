package com.agri.controller;

import com.agri.annotation.RequireLogin;
import com.agri.entity.Product;
import com.agri.service.ProductService;
import com.agri.utils.UserContext;
import com.agri.vo.Result;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/product")
@RequiredArgsConstructor
public class ProductController {

    private final ProductService productService;

    private static final int MAX_PAGE_SIZE = 100;

    private Integer validatePageSize(Integer size) {
        if (size == null || size < 1) {
            return 10;
        }
        return Math.min(size, MAX_PAGE_SIZE);
    }

    @GetMapping("/list")
    public Result<Page<Product>> list(
            @RequestParam(defaultValue = "1") Integer page,
            @RequestParam(defaultValue = "10") Integer size,
            @RequestParam(required = false) String category,
            @RequestParam(required = false) String keyword) {
        size = validatePageSize(size);
        return Result.success(productService.getProductPage(page, size, category, keyword));
    }

    @GetMapping("/{id}")
    public Result<Product> detail(@PathVariable Long id) {
        Product product = productService.getProductDetail(id);
        if (product != null) {
            return Result.success(product);
        }
        return Result.error("产品不存在");
    }

    @GetMapping("/my/{userId}")
    @RequireLogin
    public Result<Page<Product>> myProducts(
            @PathVariable Long userId,
            @RequestParam(defaultValue = "1") Integer page,
            @RequestParam(defaultValue = "10") Integer size) {
        size = validatePageSize(size);
        Long currentUserId = UserContext.getCurrentUserId();
        if (!userId.equals(currentUserId)) {
            return Result.error("无权查看");
        }
        return Result.success(productService.getMyProducts(userId, page, size));
    }

    @PostMapping
    @RequireLogin
    public Result<?> publish(@RequestBody Product product) {
        Long userId = UserContext.getCurrentUserId();
        product.setUserId(userId);
        if (productService.publishProduct(product)) {
            return Result.success("发布成功");
        }
        return Result.error("发布失败");
    }

    @PutMapping("/{id}")
    @RequireLogin
    public Result<?> update(@PathVariable Long id, @RequestBody Product product) {
        Long userId = UserContext.getCurrentUserId();
        product.setId(id);
        if (productService.updateProduct(product, userId)) {
            return Result.success("更新成功");
        }
        return Result.error("更新失败或无权限");
    }

    @DeleteMapping("/{id}")
    @RequireLogin
    public Result<?> delete(@PathVariable Long id) {
        Long userId = UserContext.getCurrentUserId();
        if (productService.deleteProduct(id, userId)) {
            return Result.success("删除成功");
        }
        return Result.error("删除失败或无权限");
    }
}
