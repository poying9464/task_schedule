package org.poying.service;

import com.baomidou.mybatisplus.extension.service.IService;
import org.poying.vo.User;

public interface UserService extends IService<User> {
    /**
     * 用户登录验证
     * @param username 用户名
     * @param password 密码
     * @return 登录成功的用户对象，验证失败返回null
     */
    User login(String username, String password);

    /**
     * 修改用户密码
     * @param userId 用户ID
     * @param oldPassword 旧密码
     * @param newPassword 新密码
     * @return 修改成功返回true，否则返回false
     */
    boolean changePassword(Long userId, String oldPassword, String newPassword);
    
    /**
     * 根据用户名查找用户
     * @param username 用户名
     * @return 用户对象，不存在返回null
     */
    User findByUsername(String username);
}