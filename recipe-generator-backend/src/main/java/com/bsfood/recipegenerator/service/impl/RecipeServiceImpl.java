package com.bsfood.recipegenerator.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.bsfood.recipegenerator.entity.FoodMaterial;
import com.bsfood.recipegenerator.entity.Recipe;
import com.bsfood.recipegenerator.entity.UserCollection;
import com.bsfood.recipegenerator.entity.UserPreference;
import com.bsfood.recipegenerator.mapper.FoodMaterialMapper;
import com.bsfood.recipegenerator.mapper.RecipeMapper;
import com.bsfood.recipegenerator.mapper.UserCollectionMapper;
import com.bsfood.recipegenerator.service.RecipeService;
import com.bsfood.recipegenerator.service.UserService;
import com.bsfood.recipegenerator.utils.AiApiClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 食谱服务实现类
 */
@Service
public class RecipeServiceImpl implements RecipeService {

    @Autowired
    private RecipeMapper recipeMapper;

    @Autowired
    private FoodMaterialMapper foodMaterialMapper;

    @Autowired
    private AiApiClient aiApiClient;

    @Autowired
    private UserService userService;

    @Autowired
    private UserCollectionMapper userCollectionMapper;

    @Override
    public List<Recipe> generateRecipe(List<Long> foodIds, Long userId, int expectCount) {
        // 查询食材名称
        List<String> foodNames = new ArrayList<>();
        List<String> conflictFoods = new ArrayList<>();
        for (Long foodId : foodIds) {
            FoodMaterial food = foodMaterialMapper.selectById(foodId);
            if (food != null) {
                foodNames.add(food.getName() + " " + food.getQuantity() + "g");
            }
        }
        if (foodNames.isEmpty()) {
            throw new IllegalArgumentException("未找到有效食材");
        }

        // 查询用户偏好
        String difficultyLevel = null;
        String tastePreference = null;
        String cookingLevel = null;
        Integer maxCookingTime = null;
        String dietLimit = null;
        String healthGoal = null;
        UserPreference preference = userService.getPreference(userId);
        if (preference != null) {
            tastePreference = preference.getTastePreference();
            cookingLevel = preference.getCookingLevel();
            dietLimit = preference.getDietLimit();
            healthGoal = preference.getHealthGoal();
        }

        // 检查食材是否与饮食限制冲突
        if (dietLimit != null && !dietLimit.isEmpty()) {
            for (String foodName : foodNames) {
                String name = foodName.split(" ")[0];
                if (dietLimit.contains(name)) {
                    conflictFoods.add(name);
                }
            }
            if (!conflictFoods.isEmpty()) {
                throw new IllegalArgumentException("以下食材与您的饮食限制冲突：" + String.join("、", conflictFoods) + "。请移除这些食材后重试。");
            }
        }

        // 调用AI生成食谱
        List<Recipe> recipeList = aiApiClient.generateRecipe(
                foodNames, userId, expectCount, difficultyLevel, tastePreference, cookingLevel, maxCookingTime, dietLimit, healthGoal);

        // 保存生成的食谱
        for (Recipe recipe : recipeList) {
            recipe.setCreateTime(new Date());
            recipe.setFoodIds(foodIds.toString());
            recipeMapper.insert(recipe);
        }

        return recipeList;
    }

    @Override
    public boolean saveRecipe(Recipe recipe) {
        recipe.setCreateTime(new Date());
        return recipeMapper.insert(recipe) > 0;
    }

    @Override
    public List<Recipe> getRecipeList(Long userId) {
        QueryWrapper<Recipe> wrapper = new QueryWrapper<>();
        wrapper.eq("user_id", userId);
        wrapper.orderByDesc("create_time");
        return recipeMapper.selectList(wrapper);
    }

    @Override
    public Recipe getRecipeById(Long id) {
        return recipeMapper.selectById(id);
    }

    @Override
    public boolean updateRecipe(Recipe recipe) {
        return recipeMapper.updateById(recipe) > 0;
    }

    @Override
    public boolean deleteRecipe(Long id) {
        return recipeMapper.deleteById(id) > 0;
    }

    @Override
    public boolean collectRecipe(Long id) {
        Recipe recipe = recipeMapper.selectById(id);
        if (recipe != null) {
            recipe.setCollectCount(recipe.getCollectCount() + 1);
            return recipeMapper.updateById(recipe) > 0;
        }
        return false;
    }

    @Override
    public boolean collectRecipe(Long recipeId, Long userId) {
        // 检查是否已收藏
        QueryWrapper<UserCollection> wrapper = new QueryWrapper<>();
        wrapper.eq("user_id", userId);
        wrapper.eq("recipe_id", recipeId);
        UserCollection existing = userCollectionMapper.selectOne(wrapper);
        if (existing != null) {
            // 已收藏，取消收藏
            userCollectionMapper.deleteById(existing.getId());
            return true;
        }
        // 未收藏，添加收藏
        UserCollection collection = new UserCollection();
        collection.setUserId(userId);
        collection.setRecipeId(recipeId);
        collection.setCreateTime(new Date());
        userCollectionMapper.insert(collection);
        return true;
    }

    @Override
    public List<Recipe> getCollectedRecipes(Long userId) {
        QueryWrapper<UserCollection> wrapper = new QueryWrapper<>();
        wrapper.eq("user_id", userId);
        wrapper.orderByDesc("create_time");
        List<UserCollection> collections = userCollectionMapper.selectList(wrapper);
        if (collections.isEmpty()) {
            return new ArrayList<>();
        }
        List<Long> recipeIds = collections.stream().map(UserCollection::getRecipeId).collect(Collectors.toList());
        return recipeMapper.selectBatchIds(recipeIds);
    }
}
