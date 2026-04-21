package com.agri.controller;

import com.agri.annotation.RequireLogin;
import com.agri.entity.User;
import com.agri.service.UserService;
import com.agri.utils.UserContext;
import com.agri.vo.LoginVo;
import com.agri.vo.Result;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/user")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @PostMapping("/register")
    public Result<?> register(@Valid @RequestBody User user) {
        if (userService.register(user)) {
            return Result.success("注册成功");
        }
        return Result.error("用户名已存在");
    }

    @PostMapping("/login")
    public Result<LoginVo> login(@RequestBody Map<String, String> params) {
        String username = params.get("username");
        String password = params.get("password");
        
        LoginVo loginVo = userService.login(username, password);
        if (loginVo != null) {
            return Result.success(loginVo);
        }
        return Result.error("用户名或密码错误");
    }

    @GetMapping("/{id}")
    public Result<User> getUserInfo(@PathVariable Long id) {
        User user = userService.getById(id);
        if (user != null) {
            user.setPassword(null);
            return Result.success(user);
        }
        return Result.error("用户不存在");
    }

    @PutMapping("/{id}")
    @RequireLogin
    public Result<?> updateUser(@PathVariable Long id, @RequestBody User user) {
        Long userId = UserContext.getCurrentUserId();
        if (!id.equals(userId)) {
            return Result.error("无权修改");
        }
        user.setId(id);
        user.setPassword(null);
        user.setRole(null);
        if (userService.updateById(user)) {
            return Result.success("修改成功");
        }
        return Result.error("修改失败");
    }

    @PutMapping("/password")
    @RequireLogin
    public Result<?> updatePassword(@RequestBody Map<String, String> params) {
        Long userId = UserContext.getCurrentUserId();
        String oldPassword = params.get("oldPassword");
        String newPassword = params.get("newPassword");
        
        if (userService.updatePassword(userId, oldPassword, newPassword)) {
            return Result.success("密码修改成功");
        }
        return Result.error("原密码错误");
    }

    @GetMapping("/info")
    @RequireLogin
    public Result<Map<String, Object>> getCurrentUser() {
        Long userId = UserContext.getCurrentUserId();
        String username = UserContext.getCurrentUsername();
        String role = UserContext.getCurrentRole();
        
        Map<String, Object> map = new HashMap<>();
        map.put("userId", userId);
        map.put("username", username);
        map.put("role", role);
        
        return Result.success(map);
    }
}
