package com.bsfood.recipegenerator.service;

import com.bsfood.recipegenerator.entity.Nutrition;

import java.util.Map;

/**
 * 营养服务接口
 */
public interface NutritionService {
    /**
     * 分析食谱营养成分
     * @param recipeId 食谱ID
     * @return 营养分析结果
     */
    Map<String, Object> analyzeNutrition(Long recipeId);
    
    /**
     * 保存营养数据
     * @param nutrition 营养数据
     * @return 保存结果
     */
    boolean saveNutrition(Nutrition nutrition);
    
    /**
     * 获取营养数据
     * @param id 营养数据ID
     * @return 营养数据
     */
    Nutrition getNutritionById(Long id);
    
    /**
     * 更新营养数据
     * @param nutrition 营养数据
     * @return 更新结果
     */
    boolean updateNutrition(Nutrition nutrition);
    
    /**
     * 删除营养数据
     * @param id 营养数据ID
     * @return 删除结果
     */
    boolean deleteNutrition(Long id);
}