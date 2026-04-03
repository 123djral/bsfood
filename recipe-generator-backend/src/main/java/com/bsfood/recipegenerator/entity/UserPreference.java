package com.bsfood.recipegenerator.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;

/**
 * 用户偏好实体类
 */
@TableName("user_preference")
public class UserPreference {
    /**
     * 偏好ID
     */
    @TableId(type = IdType.AUTO)
    private Long id;
    
    /**
     * 用户ID
     */
    private Long userId;
    
    /**
     * 健康目标
     */
    private String healthGoal;
    
    /**
     * 饮食限制
     */
    private String dietLimit;
    
    /**
     * 口味倾向
     */
    private String tastePreference;
    
    /**
     * 烹饪熟练度
     */
    private String cookingLevel;

    // Getter and Setter methods
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public String getHealthGoal() {
        return healthGoal;
    }

    public void setHealthGoal(String healthGoal) {
        this.healthGoal = healthGoal;
    }

    public String getDietLimit() {
        return dietLimit;
    }

    public void setDietLimit(String dietLimit) {
        this.dietLimit = dietLimit;
    }

    public String getTastePreference() {
        return tastePreference;
    }

    public void setTastePreference(String tastePreference) {
        this.tastePreference = tastePreference;
    }

    public String getCookingLevel() {
        return cookingLevel;
    }

    public void setCookingLevel(String cookingLevel) {
        this.cookingLevel = cookingLevel;
    }
}