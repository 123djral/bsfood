package com.bsfood.recipegenerator.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.bsfood.recipegenerator.entity.FoodMaterial;
import com.bsfood.recipegenerator.entity.Recipe;
import com.bsfood.recipegenerator.entity.UserPreference;
import com.bsfood.recipegenerator.mapper.FoodMaterialMapper;
import com.bsfood.recipegenerator.mapper.RecipeMapper;
import com.bsfood.recipegenerator.service.RecipeService;
import com.bsfood.recipegenerator.service.UserService;
import com.bsfood.recipegenerator.utils.AiApiClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

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

    @Override
    public List<Recipe> generateRecipe(List<Long> foodIds, Long userId, int expectCount) {
        // 查询食材名称
        List<String> foodNames = new ArrayList<>();
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
        UserPreference preference = userService.getPreference(userId);
        if (preference != null) {
            tastePreference = preference.getTastePreference();
            cookingLevel = preference.getCookingLevel();
        }

        // 调用AI生成食谱
        List<Recipe> recipeList = aiApiClient.generateRecipe(
                foodNames, userId, expectCount, difficultyLevel, tastePreference, cookingLevel, maxCookingTime);

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
}
