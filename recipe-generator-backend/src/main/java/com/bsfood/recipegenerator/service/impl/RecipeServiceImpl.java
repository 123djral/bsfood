package com.bsfood.recipegenerator.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.bsfood.recipegenerator.entity.Recipe;
import com.bsfood.recipegenerator.mapper.RecipeMapper;
import com.bsfood.recipegenerator.service.RecipeService;
import com.bsfood.recipegenerator.utils.AiApiClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

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
    private AiApiClient aiApiClient;
    
    @Override
    public List<Recipe> generateRecipe(List<Long> foodIds, Long userId, int expectCount) {
        // 调用 AI API 生成食谱
        List<Recipe> recipeList = aiApiClient.generateRecipe(foodIds, userId, expectCount);
        
        // 保存生成的食谱
        for (Recipe recipe : recipeList) {
            recipe.setCreateTime(new Date());
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