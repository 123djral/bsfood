package com.bsfood.recipegenerator.service;

import com.bsfood.recipegenerator.entity.Recipe;

import java.util.List;

/**
 * 食谱服务接口
 */
public interface RecipeService {
    /**
     * 生成食谱
     * @param foodIds 食材ID列表
     * @param userId 用户ID
     * @param expectCount 期望食谱数量
     * @return 食谱列表
     */
    List<Recipe> generateRecipe(List<Long> foodIds, Long userId, int expectCount);
    
    /**
     * 保存食谱
     * @param recipe 食谱信息
     * @return 保存结果
     */
    boolean saveRecipe(Recipe recipe);
    
    /**
     * 获取食谱列表
     * @param userId 用户ID
     * @return 食谱列表
     */
    List<Recipe> getRecipeList(Long userId);
    
    /**
     * 获取食谱详情
     * @param id 食谱ID
     * @return 食谱详情
     */
    Recipe getRecipeById(Long id);
    
    /**
     * 更新食谱
     * @param recipe 食谱信息
     * @return 更新结果
     */
    boolean updateRecipe(Recipe recipe);
    
    /**
     * 删除食谱
     * @param id 食谱ID
     * @return 删除结果
     */
    boolean deleteRecipe(Long id);
    
    /**
     * 收藏食谱
     * @param id 食谱ID
     * @return 收藏结果
     */
    boolean collectRecipe(Long id);
}