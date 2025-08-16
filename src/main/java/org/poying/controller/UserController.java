package org.poying.controller;

import org.poying.e.Unify;
import org.poying.service.UserService;
import org.poying.vo.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/user")
public class UserController {
    
    @Autowired
    private UserService userService;
    
    @PostMapping("/login")
    public Unify<Map<String, Object>> login(@RequestBody Map<String, String> credentials) {
        String username = credentials.get("username");
        String password = credentials.get("password");
        
        User user = userService.login(username, password);
        Map<String, Object> response = new HashMap<>();
        
        if (user != null) {
            response.put("success", true);
            response.put("message", "登录成功");
            response.put("user", Map.of("id", user.getId(), "username", user.getUsername()));
        } else {
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