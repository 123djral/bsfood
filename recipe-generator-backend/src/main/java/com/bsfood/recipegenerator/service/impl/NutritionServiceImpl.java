package com.bsfood.recipegenerator.service.impl;

import com.bsfood.recipegenerator.entity.FoodMaterial;
import com.bsfood.recipegenerator.entity.Nutrition;
import com.bsfood.recipegenerator.entity.Recipe;
import com.bsfood.recipegenerator.mapper.FoodMaterialMapper;
import com.bsfood.recipegenerator.mapper.NutritionMapper;
import com.bsfood.recipegenerator.mapper.RecipeMapper;
import com.bsfood.recipegenerator.service.NutritionService;
import com.bsfood.recipegenerator.utils.AiApiClient;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * 营养服务实现类
 */
@Service
public class NutritionServiceImpl implements NutritionService {

    @Autowired
    private NutritionMapper nutritionMapper;

    @Autowired
    private RecipeMapper recipeMapper;

    @Autowired
    private FoodMaterialMapper foodMaterialMapper;

    @Autowired
    private AiApiClient aiApiClient;

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    public Map<String, Object> analyzeNutrition(Long recipeId) {
        // 查询食谱信息
        Recipe recipe = recipeMapper.selectById(recipeId);
        if (recipe == null) {
            throw new IllegalArgumentException("食谱不存在: " + recipeId);
        }

        // 获取食材名称列表
        List<String> foodNames = new ArrayList<>();
        if (recipe.getFoodIds() != null && !recipe.getFoodIds().isEmpty()) {
            String cleaned = recipe.getFoodIds().replaceAll("[\\[\\]\\s]", "");
            for (String idStr : cleaned.split(",")) {
                try {
                    Long foodId = Long.parseLong(idStr.trim());
                    FoodMaterial food = foodMaterialMapper.selectById(foodId);
                    if (food != null) {
                        foodNames.add(food.getName());
                    }
                } catch (NumberFormatException ignored) {
                    foodNames.add(idStr.trim());
                }
            }
        }
        if (foodNames.isEmpty()) {
            foodNames.add("未知食材");
        }

        // 调用AI分析营养
        String nutritionJson = aiApiClient.analyzeNutrition(recipe.getName(), recipe.getSteps(), foodNames);

        try {
            return objectMapper.readValue(nutritionJson, Map.class);
        } catch (Exception e) {
            throw new RuntimeException("解析营养分析结果失败: " + e.getMessage(), e);
        }
    }

    @Override
    public boolean saveNutrition(Nutrition nutrition) {
        return nutritionMapper.insert(nutrition) > 0;
    }

    @Override
    public Nutrition getNutritionById(Long id) {
        return nutritionMapper.selectById(id);
    }

    @Override
    public boolean updateNutrition(Nutrition nutrition) {
        return nutritionMapper.updateById(nutrition) > 0;
    }

    @Override
    public boolean deleteNutrition(Long id) {
        return nutritionMapper.deleteById(id) > 0;
    }
}
