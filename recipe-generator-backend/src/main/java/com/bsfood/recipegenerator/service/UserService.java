package com.bsfood.recipegenerator.service;

import com.bsfood.recipegenerator.entity.User;
import com.bsfood.recipegenerator.entity.UserPreference;

/**
 * 用户服务接口
 */
public interface UserService {
    /**
     * 用户注册
     * @param user 用户信息
     * @return 注册结果
     */
    boolean register(User user);
    
    /**
     * 用户登录
     * @param username 用户名
     * @param password 密码
     * @return 用户信息
     */
    User login(String username, String password);
    
    /**
     * 更新用户信息
     * @param user 用户信息
     * @return 更新结果
     */
    boolean update(User user);
    
    /**
     * 获取用户偏好
     * @param userId 用户ID
     * @return 用户偏好
     */
    UserPreference getPreference(Long userId);
    
    /**
     * 更新用户偏好
     * @param preference 用户偏好
     * @return 更新结果
     */
    boolean updatePreference(UserPreference preference);
}