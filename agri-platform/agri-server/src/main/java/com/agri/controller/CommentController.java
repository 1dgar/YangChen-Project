package com.agri.controller;

import com.agri.annotation.RequireLogin;
import com.agri.entity.Comment;
import com.agri.service.CommentService;
import com.agri.utils.UserContext;
import com.agri.vo.Result;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/comment")
@RequiredArgsConstructor
public class CommentController {

    private final CommentService commentService;

    @GetMapping("/product/{productId}")
    public Result<Page<Comment>> productComments(
            @PathVariable Long productId,
            @RequestParam(defaultValue = "1") Integer page,
            @RequestParam(defaultValue = "10") Integer size) {
        return Result.success(commentService.getByProductId(productId, page, size));
    }

    @PostMapping
    @RequireLogin
    public Result<?> add(@RequestBody Comment comment) {
        Long userId = UserContext.getCurrentUserId();
        comment.setUserId(userId);
        if (commentService.add(comment)) {
            return Result.success("留言成功");
        }
        return Result.error("留言失败");
    }

    @PutMapping("/{id}/reply")
    @RequireLogin
    public Result<?> reply(@PathVariable Long id, @RequestBody Comment comment) {
        Long userId = UserContext.getCurrentUserId();
        if (commentService.reply(id, comment.getReply(), userId)) {
            return Result.success("回复成功");
        }
        return Result.error("回复失败或无权限");
    }

    @GetMapping("/my/{userId}")
    @RequireLogin
    public Result<Page<Comment>> myComments(
            @PathVariable Long userId,
            @RequestParam(defaultValue = "1") Integer page,
            @RequestParam(defaultValue = "10") Integer size) {
        Long currentUserId = UserContext.getCurrentUserId();
        if (!userId.equals(currentUserId)) {
            return Result.error("无权查看");
        }
        return Result.success(commentService.getByUserId(userId, page, size));
    }
}
