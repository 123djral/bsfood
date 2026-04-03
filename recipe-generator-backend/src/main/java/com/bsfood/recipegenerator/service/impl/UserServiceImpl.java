package com.bsfood.recipegenerator.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.bsfood.recipegenerator.entity.User;
import com.bsfood.recipegenerator.entity.UserPreference;
import com.bsfood.recipegenerator.mapper.UserMapper;
import com.bsfood.recipegenerator.mapper.UserPreferenceMapper;
import com.bsfood.recipegenerator.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.concurrent.TimeUnit;

/**
 * 用户服务实现类
 */
@Service
public class UserServiceImpl implements UserService {

    private static final String PREFERENCE_CACHE_PREFIX = "user:preference:";

    @Autowired
    private UserMapper userMapper;

    @Autowired
    private UserPreferenceMapper userPreferenceMapper;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private RedisTemplate<String, Object> redisTemplate;

    @Override
    public boolean register(User user) {
        QueryWrapper<User> wrapper = new QueryWrapper<>();
        wrapper.eq("username", user.getUsername());
        if (userMapper.selectOne(wrapper) != null) {
            return false;
        }
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        user.setCreateTime(new Date());
        return userMapper.insert(user) > 0;
    }

    @Override
    public User login(String username, String password) {
        QueryWrapper<User> wrapper = new QueryWrapper<>();
        wrapper.eq("username", username);
        User user = userMapper.selectOne(wrapper);
        if (user != null && passwordEncoder.matches(password, user.getPassword())) {
            return user;
        }
        return null;
    }

    @Override
    public boolean update(User user) {
        if (user.getPassword() != null && !user.getPassword().isEmpty()) {
            user.setPassword(passwordEncoder.encode(user.getPassword()));
        }
        return userMapper.updateById(user) > 0;
    }

    @Override
    public UserPreference getPreference(Long userId) {
        // 先从Redis缓存读取
        String cacheKey = PREFERENCE_CACHE_PREFIX + userId;
        try {
            Object cached = redisTemplate.opsForValue().get(cacheKey);
            if (cached instanceof UserPreference) {
                return (UserPreference) cached;
            }
        } catch (Exception ignored) {
        }

        // 缓存未命中，查询数据库
        QueryWrapper<UserPreference> wrapper = new QueryWrapper<>();
        wrapper.eq("user_id", userId);
        UserPreference preference = userPreferenceMapper.selectOne(wrapper);

        // 写入缓存
        if (preference != null) {
            try {
                redisTemplate.opsForValue().set(cacheKey, preference, 30, TimeUnit.MINUTES);
            } catch (Exception ignored) {
            }
        }
        return preference;
    }

    @Override
    public boolean updatePreference(UserPreference preference) {
        QueryWrapper<UserPreference> wrapper = new QueryWrapper<>();
        wrapper.eq("user_id", preference.getUserId());
        UserPreference existing = userPreferenceMapper.selectOne(wrapper);

        boolean success;
        if (existing != null) {
            preference.setId(existing.getId());
            success = userPreferenceMapper.updateById(preference) > 0;
        } else {
            success = userPreferenceMapper.insert(preference) > 0;
        }

        // 更新缓存
        if (success) {
            try {
                String cacheKey = PREFERENCE_CACHE_PREFIX + preference.getUserId();
                redisTemplate.opsForValue().set(cacheKey, preference, 30, TimeUnit.MINUTES);
            } catch (Exception ignored) {
            }
        }
        return success;
    }
}
