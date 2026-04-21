package com.agri.service.impl;

import com.agri.entity.User;
import com.agri.mapper.UserMapper;
import com.agri.service.UserService;
import com.agri.utils.JwtUtil;
import com.agri.vo.LoginVo;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserServiceImpl extends ServiceImpl<UserMapper, User> implements UserService {

    private final JwtUtil jwtUtil;

    @Override
    public LoginVo login(String username, String password) {
        LambdaQueryWrapper<User> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(User::getUsername, username)
               .eq(User::getStatus, 1);
        
        User user = getOne(wrapper);
        if (user == null || !password.equals(user.getPassword())) {
            log.warn("登录失败: 用户名或密码错误 - {}", username);
            return null;
        }
        
        log.info("用户登录成功: {} (role: {})", username, user.getRole());
        
        LoginVo loginVo = new LoginVo();
        loginVo.setId(user.getId());
        loginVo.setUsername(user.getUsername());
        loginVo.setRole(user.getRole());
        loginVo.setToken(jwtUtil.generateToken(user.getId(), user.getUsername(), user.getRole()));
        
        return loginVo;
    }

    @Override
    public boolean register(User user) {
        LambdaQueryWrapper<User> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(User::getUsername, user.getUsername());
        if (count(wrapper) > 0) {
            log.warn("注册失败: 用户名已存在 - {}", user.getUsername());
            return false;
        }
        user.setStatus(1);
        boolean result = save(user);
        if (result) {
            log.info("用户注册成功: {} (role: {})", user.getUsername(), user.getRole());
        }
        return result;
    }

    @Override
    public boolean updatePassword(Long userId, String oldPassword, String newPassword) {
        User user = getById(userId);
        if (user == null) {
            log.warn("修改密码失败: 用户不存在 - {}", userId);
            return false;
        }
        if (!oldPassword.equals(user.getPassword())) {
            log.warn("修改密码失败: 原密码错误 - userId: {}", userId);
            return false;
        }
        user.setPassword(newPassword);
        boolean result = updateById(user);
        if (result) {
            log.info("密码修改成功: userId: {}", userId);
        }
        return result;
    }
}
