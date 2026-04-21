package com.bsfood.recipegenerator.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
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

    /**
     * 收藏/取消收藏食谱（个人收藏）
     * @param recipeId 食谱ID
     * @param userId 用户ID
     * @return 操作结果
     */
    boolean collectRecipe(Long recipeId, Long userId);

    /**
     * 获取用户收藏的食谱列表
     * @param userId 用户ID
     * @return 收藏的食谱列表
     */
    List<Recipe> getCollectedRecipes(Long userId);

    /**
     * 分页搜索食谱
     * @param userId 用户ID
     * @param keyword 搜索关键词
     * @param page 页码
     * @param size 每页大小
     * @return 分页结果
     */
    IPage<Recipe> searchRecipes(Long userId, String keyword, int page, int size);

    /**
     * 分页搜索收藏的食谱
     * @param userId 用户ID
     * @param keyword 搜索关键词
     * @param page 页码
     * @param size 每页大小
     * @return 分页结果
     */
    IPage<Recipe> searchCollectedRecipes(Long userId, String keyword, int page, int size);

    /**
     * 根据食谱名称查询完整食谱信息（食材、厨具、步骤、营养分析）
     * @param recipeName 食谱名称
     * @return 包含食谱详情和营养分析的Map
     */
    java.util.Map<String, Object> queryRecipeByName(String recipeName);
}