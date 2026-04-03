package com.bsfood.recipegenerator.service;

import com.bsfood.recipegenerator.entity.FoodMaterial;
import com.bsfood.recipegenerator.entity.Nutrition;

import java.util.List;

/**
 * 食材服务接口
 */
public interface FoodService {
    /**
     * 识别食材
     * @param text 文本食材信息
     * @param image 图像食材信息
     * @param type 输入类型
     * @return 识别结果
     */
    List<FoodMaterial> recognizeFood(String text, String image, String type);
    
    /**
     * 保存食材
     * @param foodMaterial 食材信息
     * @return 保存结果
     */
    boolean saveFood(FoodMaterial foodMaterial);
    
    /**
     * 获取食材列表
     * @return 食材列表
     */
    List<FoodMaterial> getFoodList();
    
    /**
     * 获取食材详情
     * @param id 食材ID
     * @return 食材详情
     */
    FoodMaterial getFoodById(Long id);
    
    /**
     * 更新食材
     * @param foodMaterial 食材信息
     * @return 更新结果
     */
    boolean updateFood(FoodMaterial foodMaterial);
    
    /**
     * 删除食材
     * @param id 食材ID
     * @return 删除结果
     */
    boolean deleteFood(Long id);
    
    /**
     * 获取食材营养数据
     * @param foodId 食材ID
     * @return 营养数据
     */
    Nutrition getNutritionByFoodId(Long foodId);
    
    /**
     * 推荐食材替代方案
     * @param foodId 食材ID
     * @return 替代食材列表
     */
    List<FoodMaterial> getSubstituteFood(Long foodId);
}