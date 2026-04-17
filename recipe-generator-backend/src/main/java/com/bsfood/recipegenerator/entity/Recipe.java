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

    /**
     * 英文名称（用于图片搜索）
     */
    private String englishName;

    /**
     * 图片搜索关键词（AI优化，用于在Spoonacular等API搜索匹配图片）
     */
    private String imageKeyword;

    /**
     * AI生成的食谱图片URL
     */
    private String imageUrl;

    /**
     * 详细食材列表（JSON字符串，每个元素包含name和quantity）
     */
    private String ingredients;

    /**
     * 所需厨具工具列表（JSON字符串）
     */
    private String tools;

    /**
     * 烹饪小贴士
     */
    private String tips;

    /**
     * 适宜人群
     */
    private String suitableCrowd;

    /**
     * 菜系
     */
    private String cuisineStyle;

    /**
     * 口味特点
     */
    private String flavorProfile;

    /**
     * 几人份
     */
    private Integer servings;

    /**
     * 简要营养信息
     */
    private String nutritionBrief;

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

    public String getEnglishName() {
        return englishName;
    }

    public void setEnglishName(String englishName) {
        this.englishName = englishName;
    }

    public String getImageKeyword() {
        return imageKeyword;
    }

    public void setImageKeyword(String imageKeyword) {
        this.imageKeyword = imageKeyword;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public String getIngredients() {
        return ingredients;
    }

    public void setIngredients(String ingredients) {
        this.ingredients = ingredients;
    }

    public String getTools() {
        return tools;
    }

    public void setTools(String tools) {
        this.tools = tools;
    }

    public String getTips() {
        return tips;
    }

    public void setTips(String tips) {
        this.tips = tips;
    }

    public String getSuitableCrowd() {
        return suitableCrowd;
    }

    public void setSuitableCrowd(String suitableCrowd) {
        this.suitableCrowd = suitableCrowd;
    }

    public String getCuisineStyle() {
        return cuisineStyle;
    }

    public void setCuisineStyle(String cuisineStyle) {
        this.cuisineStyle = cuisineStyle;
    }

    public String getFlavorProfile() {
        return flavorProfile;
    }

    public void setFlavorProfile(String flavorProfile) {
        this.flavorProfile = flavorProfile;
    }

    public Integer getServings() {
        return servings;
    }

    public void setServings(Integer servings) {
        this.servings = servings;
    }

    public String getNutritionBrief() {
        return nutritionBrief;
    }

    public void setNutritionBrief(String nutritionBrief) {
        this.nutritionBrief = nutritionBrief;
    }
}