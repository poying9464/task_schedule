package org.poying.controller;

import org.poying.e.Unify;
import org.poying.service.UserService;
import org.poying.vo.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/user")
public class UserController {

    private static final Logger log = LoggerFactory.getLogger(UserController.class);
    @Autowired
    private UserService userService;
    
    @Autowired
    private AuthenticationManager authenticationManager;


    @PostMapping("/login")
    public Unify<Map<String, Object>> login(@RequestBody Map<String, String> credentials) {
        String username = credentials.get("username");
        String password = credentials.get("password");
        
        Map<String, Object> response = new HashMap<>();
        
        try {
            // 使用Spring Security的AuthenticationManager进行认证
            Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(username, password)
            );
            
            // 将认证对象设置到SecurityContext中，完成登录
            SecurityContextHolder.getContext().setAuthentication(authentication);
            // 获取当前登录用户的token

            // 获取用户信息
            User user = userService.findByUsername(username);
            
            response.put("success", true);
            response.put("message", "登录成功");
            response.put("user", Map.of("id", user.getId(), "username", user.getUsername()));
        } catch (Exception e) {
            log.error("登录失败: ", e);
            response.put("success", false);
            response.put("message", "用户名或密码错误");
        }
        
        return Unify.success(response);
    }
    
    @PostMapping("/change-password")
    public ResponseEntity<Map<String, Object>> changePassword(@RequestBody Map<String, String> passwordData) {
        Long userId = Long.valueOf(passwordData.get("userId"));
        String oldPassword = passwordData.get("oldPassword");
        String newPassword = passwordData.get("newPassword");
        
        boolean result = userService.changePassword(userId, oldPassword, newPassword);
        Map<String, Object> response = new HashMap<>();
        
        if (result) {
            response.put("success", true);
            response.put("message", "密码修改成功");
        } else {
            response.put("success", false);
            response.put("message", "密码修改失败，请检查原密码是否正确");
        }
        
        return ResponseEntity.ok(response);
    }
}