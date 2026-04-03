package com.bsfood.recipegenerator.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;

import java.util.Date;

/**
 * 食谱实体类
 */
@TableName("recipe")
public class Recipe {
    /**
     * 食谱ID
     */
    @TableId(type = IdType.AUTO)
    private Long id;
    
    /**
     * 食谱名称
     */
    private String name;
    
    /**
     * 用户ID
     */
    private Long userId;
    
    /**
     * 烹饪耗时（分钟）
     */
    private Integer cookingTime;
    
    /**
     * 难度等级
     */
    private String difficultyLevel;
    
    /**
     * 烹饪步骤
     */
    private String steps;
    
    /**
     * 食材ID列表
     */
    private String foodIds;
    
    /**
     * 收藏次数
     */
    private Integer collectCount;
    
    /**
     * 创建时间
     */
    private Date createTime;

    // Getter and Setter methods
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public Integer getCookingTime() {
        return cookingTime;
    }

    public void setCookingTime(Integer cookingTime) {
        this.cookingTime = cookingTime;
    }

    public String getDifficultyLevel() {
        return difficultyLevel;
    }

    public void setDifficultyLevel(String difficultyLevel) {
        this.difficultyLevel = difficultyLevel;
    }

    public String getSteps() {
        return steps;
    }

    public void setSteps(String steps) {
        this.steps = steps;
    }

    public String getFoodIds() {
        return foodIds;
    }

    public void setFoodIds(String foodIds) {
        this.foodIds = foodIds;
    }

    public Integer getCollectCount() {
        return collectCount;
    }

    public void setCollectCount(Integer collectCount) {
        this.collectCount = collectCount;
    }

    public Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }
}