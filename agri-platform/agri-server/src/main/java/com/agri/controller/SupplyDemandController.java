package com.agri.controller;

import com.agri.annotation.RequireLogin;
import com.agri.entity.SupplyDemand;
import com.agri.service.SupplyDemandService;
import com.agri.utils.UserContext;
import com.agri.vo.Result;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/supply-demand")
@RequiredArgsConstructor
public class SupplyDemandController {

    private final SupplyDemandService supplyDemandService;

    private static final int MAX_PAGE_SIZE = 100;

    private Integer validatePageSize(Integer size) {
        if (size == null || size < 1) {
            return 10;
        }
        return Math.min(size, MAX_PAGE_SIZE);
    }

    @GetMapping("/list")
    public Result<Page<SupplyDemand>> list(
            @RequestParam(defaultValue = "1") Integer page,
            @RequestParam(defaultValue = "10") Integer size,
            @RequestParam(required = false) String type,
            @RequestParam(required = false) String category) {
        size = validatePageSize(size);
        return Result.success(supplyDemandService.getPage(page, size, type, category));
    }

    @GetMapping("/{id}")
    public Result<SupplyDemand> detail(@PathVariable Long id) {
        SupplyDemand sd = supplyDemandService.getDetail(id);
        if (sd != null) {
            return Result.success(sd);
        }
        return Result.error("信息不存在");
    }

    @GetMapping("/my/{userId}")
    @RequireLogin
    public Result<Page<SupplyDemand>> myList(
            @PathVariable Long userId,
            @RequestParam(defaultValue = "1") Integer page,
            @RequestParam(defaultValue = "10") Integer size) {
        size = validatePageSize(size);
        Long currentUserId = UserContext.getCurrentUserId();
        if (!userId.equals(currentUserId)) {
            return Result.error("无权查看");
        }
        return Result.success(supplyDemandService.getByUserId(userId, page, size));
    }

    @PostMapping
    @RequireLogin
    public Result<?> publish(@RequestBody SupplyDemand supplyDemand) {
        Long userId = UserContext.getCurrentUserId();
        supplyDemand.setUserId(userId);
        System.out.println("收到发布请求: " + supplyDemand);
        if (supplyDemandService.publish(supplyDemand)) {
            System.out.println("发布成功，id: " + supplyDemand.getId());
            return Result.success("发布成功");
        }
        System.out.println("发布失败");
        return Result.error("发布失败");
    }

    @PutMapping("/{id}")
    @RequireLogin
    public Result<?> update(@PathVariable Long id, @RequestBody SupplyDemand supplyDemand) {
        Long userId = UserContext.getCurrentUserId();
        supplyDemand.setId(id);
        if (supplyDemandService.update(supplyDemand, userId)) {
            return Result.success("更新成功");
        }
        return Result.error("更新失败或无权限");
    }

    @DeleteMapping("/{id}")
    @RequireLogin
    public Result<?> delete(@PathVariable Long id) {
        Long userId = UserContext.getCurrentUserId();
        if (supplyDemandService.delete(id, userId)) {
            return Result.success("删除成功");
        }
        return Result.error("删除失败或无权限");
    }
}
