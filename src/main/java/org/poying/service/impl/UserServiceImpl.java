package org.poying.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.poying.mapper.UserMapper;
import org.poying.service.UserService;
import org.poying.util.PasswordUtil;
import org.poying.vo.User;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

@Service
public class UserServiceImpl extends ServiceImpl<UserMapper, User> implements UserService {
    
    @Override
    public User login(String username, String password) {
        if (!StringUtils.hasText(username) || !StringUtils.hasText(password)) {
            return null;
        }
        
        User user = findByUsername(username);
        if (user == null) {
            return null;
        }
        
        // 验证密码
        if (PasswordUtil.matches(password, user.getPassword())) {
            return user;
        }
        
        return null;
    }
    
    @Override
    public boolean changePassword(Long userId, String oldPassword, String newPassword) {
        User user = getById(userId);
        if (user == null) {
            return false;
        }
        
        // 验证旧密码
        if (!PasswordUtil.matches(oldPassword, user.getPassword())) {
            return false;
        }
        
        // 更新密码
        user.setPassword(PasswordUtil.encode(newPassword));
        return updateById(user);
    }
    
    @Override
    public User findByUsername(String username) {
        return getOne(
            new com.baomidou.mybatisplus.core.conditions.query.QueryWrapper<User>()
                .eq("username", username)
        );
    }
}