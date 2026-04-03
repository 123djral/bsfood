package com.bsfood.recipegenerator.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.bsfood.recipegenerator.entity.User;
import com.bsfood.recipegenerator.entity.UserPreference;
import com.bsfood.recipegenerator.mapper.UserMapper;
import com.bsfood.recipegenerator.mapper.UserPreferenceMapper;
import com.bsfood.recipegenerator.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;

/**
 * 用户服务实现类
 */
@Service
public class UserServiceImpl implements UserService {
    
    @Autowired
    private UserMapper userMapper;
    
    @Autowired
    private UserPreferenceMapper userPreferenceMapper;
    
    @Override
    public boolean register(User user) {
        // 检查用户名是否已存在
        QueryWrapper<User> wrapper = new QueryWrapper<>();
        wrapper.eq("username", user.getUsername());
        if (userMapper.selectOne(wrapper) != null) {
            return false;
        }
        // 设置创建时间
        user.setCreateTime(new Date());
        // 保存用户信息
        return userMapper.insert(user) > 0;
    }
    
    @Override
    public User login(String username, String password) {
        QueryWrapper<User> wrapper = new QueryWrapper<>();
        wrapper.eq("username", username).eq("password", password);
        return userMapper.selectOne(wrapper);
    }
    
    @Override
    public boolean update(User user) {
        return userMapper.updateById(user) > 0;
    }
    
    @Override
    public UserPreference getPreference(Long userId) {
        QueryWrapper<UserPreference> wrapper = new QueryWrapper<>();
        wrapper.eq("user_id", userId);
        return userPreferenceMapper.selectOne(wrapper);
    }
    
    @Override
    public boolean updatePreference(UserPreference preference) {
        QueryWrapper<UserPreference> wrapper = new QueryWrapper<>();
        wrapper.eq("user_id", preference.getUserId());
        UserPreference existing = userPreferenceMapper.selectOne(wrapper);
        if (existing != null) {
            // 更新现有记录
            preference.setId(existing.getId());
            return userPreferenceMapper.updateById(preference) > 0;
        } else {
            // 创建新记录
            return userPreferenceMapper.insert(preference) > 0;
        }
    }
}