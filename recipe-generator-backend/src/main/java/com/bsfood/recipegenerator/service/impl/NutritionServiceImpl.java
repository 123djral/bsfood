package com.bsfood.recipegenerator.service.impl;

import com.bsfood.recipegenerator.entity.Nutrition;
import com.bsfood.recipegenerator.mapper.NutritionMapper;
import com.bsfood.recipegenerator.service.NutritionService;
import com.bsfood.recipegenerator.utils.AiApiClient;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Map;

/**
 * 营养服务实现类
 */
@Service
public class NutritionServiceImpl implements NutritionService {
    
    @Autowired
    private NutritionMapper nutritionMapper;
    
    @Autowired
    private AiApiClient aiApiClient;
    
    private final ObjectMapper objectMapper = new ObjectMapper();
    
    @Override
    public Map<String, Object> analyzeNutrition(Long recipeId) {
        // 调用 AI API 分析营养成分
        String nutritionJson = aiApiClient.analyzeNutrition(recipeId);
        
        try {
            // 解析 JSON 结果
            return objectMapper.readValue(nutritionJson, Map.class);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
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