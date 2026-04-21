package com.agri.service;

import com.agri.entity.User;
import com.agri.vo.LoginVo;
import com.baomidou.mybatisplus.extension.service.IService;

public interface UserService extends IService<User> {
    
    LoginVo login(String username, String password);
    
    boolean register(User user);
    
    boolean updatePassword(Long userId, String oldPassword, String newPassword);
}
