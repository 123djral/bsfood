package com.bsfood.recipegenerator.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.bsfood.recipegenerator.entity.FoodMaterial;
import com.bsfood.recipegenerator.entity.Nutrition;
import com.bsfood.recipegenerator.mapper.FoodMaterialMapper;
import com.bsfood.recipegenerator.mapper.NutritionMapper;
import com.bsfood.recipegenerator.service.FoodService;
import com.bsfood.recipegenerator.utils.AiApiClient;
import com.bsfood.recipegenerator.utils.McpImageClient;
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

    @Autowired
    private McpImageClient mcpImageClient;

    @Override
    public List<FoodMaterial> recognizeFood(String text, String image, String type, Long userId) {
        List<FoodMaterial> foodList;

        if ("text".equals(type)) {
            foodList = aiApiClient.recognizeTextFood(text);
        } else if ("image".equals(type)) {
            // 使用 Qwen-VL 图像理解
            foodList = aiApiClient.recognizeImageFood(image);
        } else if ("mix".equals(type)) {
            // 混合模式：先通过 Qwen-VL 识别图片
            List<FoodMaterial> imageFoods = aiApiClient.recognizeImageFood(image);
            // 再通过 AI 识别文本
            List<FoodMaterial> textFoods = aiApiClient.recognizeTextFood(text);
            // 合并结果
            foodList = new ArrayList<>(imageFoods);
            for (FoodMaterial tf : textFoods) {
                boolean found = false;
                for (FoodMaterial existing : foodList) {
                    if (existing.getName().equals(tf.getName())) {
                        found = true;
                        break;
                    }
                }
                if (!found) {
                    foodList.add(tf);
                }
            }
        } else {
            throw new IllegalArgumentException("不支持的输入类型: " + type);
        }

        // 保存识别结果（绑定用户ID）
        for (FoodMaterial food : foodList) {
            food.setCreateTime(new Date());
            food.setUserId(userId);
            foodMaterialMapper.insert(food);
        }

        return foodList;
    }

    @Override
    public boolean saveFood(FoodMaterial foodMaterial, Long userId) {
        foodMaterial.setUserId(userId);
        foodMaterial.setCreateTime(new Date());
        return foodMaterialMapper.insert(foodMaterial) > 0;
    }

    @Override
    public List<FoodMaterial> getFoodList(Long userId) {
        QueryWrapper<FoodMaterial> wrapper = new QueryWrapper<>();
        wrapper.eq("user_id", userId);
        wrapper.orderByDesc("create_time");
        return foodMaterialMapper.selectList(wrapper);
    }

    @Override
    public FoodMaterial getFoodById(Long id, Long userId) {
        QueryWrapper<FoodMaterial> wrapper = new QueryWrapper<>();
        wrapper.eq("id", id).eq("user_id", userId);
        return foodMaterialMapper.selectOne(wrapper);
    }

    @Override
    public boolean updateFood(FoodMaterial foodMaterial, Long userId) {
        // 确保只能更新自己的食材
        QueryWrapper<FoodMaterial> wrapper = new QueryWrapper<>();
        wrapper.eq("id", foodMaterial.getId()).eq("user_id", userId);
        FoodMaterial existing = foodMaterialMapper.selectOne(wrapper);
        if (existing == null) {
            return false;
        }
        foodMaterial.setUserId(userId);
        return foodMaterialMapper.updateById(foodMaterial) > 0;
    }

    @Override
    public boolean deleteFood(Long id, Long userId) {
        // 确保只能删除自己的食材
        QueryWrapper<FoodMaterial> wrapper = new QueryWrapper<>();
        wrapper.eq("id", id).eq("user_id", userId);
        return foodMaterialMapper.delete(wrapper) > 0;
    }

    @Override
    public Nutrition getNutritionByFoodId(Long foodId) {
        QueryWrapper<Nutrition> wrapper = new QueryWrapper<>();
        wrapper.eq("food_id", foodId);
        return nutritionMapper.selectOne(wrapper);
    }

    @Override
    public List<FoodMaterial> getSubstituteFood(Long foodId) {
        FoodMaterial originalFood = foodMaterialMapper.selectById(foodId);
        if (originalFood == null) {
            return new ArrayList<>();
        }
        // 调用AI推荐替代食材
        return aiApiClient.recommendSubstitute(originalFood.getName(), originalFood.getType());
    }

    @Override
    public List<FoodMaterial> getSubstituteByName(String foodName, String foodType) {
        return aiApiClient.recommendSubstitute(foodName, foodType);
    }
}
