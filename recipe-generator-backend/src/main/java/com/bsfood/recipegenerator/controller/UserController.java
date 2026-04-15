package com.bsfood.recipegenerator.controller;

import com.bsfood.recipegenerator.entity.User;
import com.bsfood.recipegenerator.entity.UserPreference;
import com.bsfood.recipegenerator.service.UserService;
import com.bsfood.recipegenerator.utils.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

/**
 * 用户控制器
 */
@RestController
@RequestMapping("/api/user")
public class UserController {

    @Autowired
    private UserService userService;

    @Autowired
    private JwtUtil jwtUtil;

    /**
     * 用户注册
     * @param user 用户信息
     * @return 注册结果
     */
    @PostMapping("/register")
    public Map<String, Object> register(@RequestBody User user) {
        Map<String, Object> result = new HashMap<>();
        boolean success = userService.register(user);
        if (success) {
            result.put("code", 200);
            result.put("message", "注册成功");
            // 注册后自动生成 token
            String token = jwtUtil.generateToken(user.getId(), user.getUsername());
            result.put("data", Map.of(
                "token", token,
                "userId", user.getId(),
                "username", user.getUsername(),
                "role", user.getRole()
            ));
        } else {
            result.put("code", 400);
            result.put("message", "注册失败，用户名已存在");
        }
        return result;
    }

    /**
     * 用户登录
     * @param username 用户名
     * @param password 密码
     * @return 登录结果
     */
    @PostMapping("/login")
    public Map<String, Object> login(@RequestParam String username, @RequestParam String password) {
        Map<String, Object> result = new HashMap<>();
        User user = userService.login(username, password);
        if (user != null) {
            // 生成 JWT token
            String token = jwtUtil.generateToken(user.getId(), user.getUsername());
            result.put("code", 200);
            result.put("message", "登录成功");
            result.put("data", Map.of(
                "token", token,
                "userId", user.getId(),
                "username", user.getUsername(),
                "role", user.getRole()
            ));
        } else {
            result.put("code", 400);
            result.put("message", "登录失败，用户名或密码错误");
        }
        return result;
    }

    /**
     * 更新用户信息
     * @param user 用户信息
     * @return 更新结果
     */
    @PutMapping("/update")
    public Map<String, Object> update(@RequestBody User user) {
        Map<String, Object> result = new HashMap<>();
        boolean success = userService.update(user);
        if (success) {
            result.put("code", 200);
            result.put("message", "更新成功");
        } else {
            result.put("code", 400);
            result.put("message", "更新失败");
        }
        return result;
    }

    /**
     * 获取用户偏好
     * @param userId 用户ID
     * @return 用户偏好
     */
    @GetMapping("/preference")
    public Map<String, Object> getPreference(@RequestParam Long userId) {
        Map<String, Object> result = new HashMap<>();
        UserPreference preference = userService.getPreference(userId);
        result.put("code", 200);
        result.put("message", "获取成功");
        result.put("data", preference);
        return result;
    }

    /**
     * 更新用户偏好
     * @param preference 用户偏好
     * @return 更新结果
     */
    @PutMapping("/preference")
    public Map<String, Object> updatePreference(@RequestBody UserPreference preference) {
        Map<String, Object> result = new HashMap<>();
        boolean success = userService.updatePreference(preference);
        if (success) {
            result.put("code", 200);
            result.put("message", "更新成功");
        } else {
            result.put("code", 400);
            result.put("message", "更新失败");
        }
        return result;
    }

    /**
     * 获取当前用户信息（从 Token 中获取）
     */
    @GetMapping("/me")
    public Map<String, Object> getCurrentUser(@RequestHeader(value = "Authorization", required = false) String authHeader) {
        Map<String, Object> result = new HashMap<>();
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            result.put("code", 401);
            result.put("message", "未登录");
            return result;
        }
        String token = authHeader.substring(7);
        Long userId = jwtUtil.getUserIdFromToken(token);
        if (userId == null) {
            result.put("code", 401);
            result.put("message", "Token无效");
            return result;
        }
        User user = userService.getUserById(userId);
        if (user != null) {
            result.put("code", 200);
            result.put("message", "获取成功");
            result.put("data", user);
        } else {
            result.put("code", 404);
            result.put("message", "用户不存在");
        }
        return result;
    }
}