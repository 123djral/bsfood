package com.bsfood.recipegenerator.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.bsfood.recipegenerator.entity.FoodMaterial;
import com.bsfood.recipegenerator.entity.Nutrition;
import com.bsfood.recipegenerator.mapper.FoodMaterialMapper;
import com.bsfood.recipegenerator.mapper.NutritionMapper;
import com.bsfood.recipegenerator.service.FoodService;
import com.bsfood.recipegenerator.utils.AiApiClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * 食材服务实现类
 */
@Service
public class FoodServiceImpl implements FoodService {
    
    @Autowired
    private FoodMaterialMapper foodMaterialMapper;
    
    @Autowired
    private NutritionMapper nutritionMapper;
    
    @Autowired
    private AiApiClient aiApiClient;
    
    @Override
    public List<FoodMaterial> recognizeFood(String text, String image, String type) {
        List<FoodMaterial> foodList = new ArrayList<>();
        
        // 根据输入类型调用不同的识别方法
        if ("text".equals(type)) {
            // 文本识别
            foodList = aiApiClient.recognizeTextFood(text);
        } else if ("image".equals(type)) {
            // 图像识别
            foodList = aiApiClient.recognizeImageFood(image);
        } else if ("mix".equals(type)) {
            // 混合识别
            foodList = aiApiClient.recognizeMixFood(text, image);
        }
        
        // 保存识别结果
        for (FoodMaterial food : foodList) {
            food.setCreateTime(new Date());
            foodMaterialMapper.insert(food);
        }
        
        return foodList;
    }
    
    @Override
    public boolean saveFood(FoodMaterial foodMaterial) {
        foodMaterial.setCreateTime(new Date());
        return foodMaterialMapper.insert(foodMaterial) > 0;
    }
    
    @Override
    public List<FoodMaterial> getFoodList() {
        return foodMaterialMapper.selectList(null);
    }
    
    @Override
    public FoodMaterial getFoodById(Long id) {
        return foodMaterialMapper.selectById(id);
    }
    
    @Override
    public boolean updateFood(FoodMaterial foodMaterial) {
        return foodMaterialMapper.updateById(foodMaterial) > 0;
    }
    
    @Override
    public boolean deleteFood(Long id) {
        return foodMaterialMapper.deleteById(id) > 0;
    }
    
    @Override
    public Nutrition getNutritionByFoodId(Long foodId) {
        QueryWrapper<Nutrition> wrapper = new QueryWrapper<>();
        wrapper.eq("food_id", foodId);
        return nutritionMapper.selectOne(wrapper);
    }
    
    @Override
    public List<FoodMaterial> getSubstituteFood(Long foodId) {
        // 获取原食材
        FoodMaterial originalFood = foodMaterialMapper.selectById(foodId);
        if (originalFood == null) {
            return new ArrayList<>();
        }
        
        // 根据食材类别推荐替代食材
        QueryWrapper<FoodMaterial> wrapper = new QueryWrapper<>();
        wrapper.eq("type", originalFood.getType()).ne("id", foodId);
        return foodMaterialMapper.selectList(wrapper);
    }
}