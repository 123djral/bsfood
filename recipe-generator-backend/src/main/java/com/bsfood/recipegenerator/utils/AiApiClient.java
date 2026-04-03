package com.bsfood.recipegenerator.utils;

import com.bsfood.recipegenerator.entity.FoodMaterial;
import com.bsfood.recipegenerator.entity.Recipe;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.ArrayList;
import java.util.List;

/**
 * AI API 客户端
 */
@Component
public class AiApiClient {
    
    @Value("${ai.api-key}")
    private String apiKey;
    
    @Value("${ai.base-url}")
    private String baseUrl;
    
    private final HttpClient httpClient = HttpClient.newHttpClient();
    
    /**
     * 识别文本食材
     * @param text 文本食材信息
     * @return 食材列表
     */
    public List<FoodMaterial> recognizeTextFood(String text) {
        // 调用 AI API 识别文本食材
        // 这里使用模拟数据，实际项目中需要调用真实的 API
        List<FoodMaterial> foodList = new ArrayList<>();
        
        // 模拟识别结果
        FoodMaterial food1 = new FoodMaterial();
        food1.setName("西红柿");
        food1.setType("蔬菜");
        food1.setQuantity(200.0);
        food1.setShelfLife(7);
        foodList.add(food1);
        
        FoodMaterial food2 = new FoodMaterial();
        food2.setName("鸡蛋");
        food2.setType("蛋类");
        food2.setQuantity(2.0);
        food2.setShelfLife(30);
        foodList.add(food2);
        
        return foodList;
    }
    
    /**
     * 识别图像食材
     * @param image 图像食材信息
     * @return 食材列表
     */
    public List<FoodMaterial> recognizeImageFood(String image) {
        // 调用 AI API 识别图像食材
        // 这里使用模拟数据，实际项目中需要调用真实的 API
        List<FoodMaterial> foodList = new ArrayList<>();
        
        // 模拟识别结果
        FoodMaterial food1 = new FoodMaterial();
        food1.setName("青菜");
        food1.setType("蔬菜");
        food1.setQuantity(100.0);
        food1.setShelfLife(3);
        foodList.add(food1);
        
        return foodList;
    }
    
    /**
     * 混合识别食材
     * @param text 文本食材信息
     * @param image 图像食材信息
     * @return 食材列表
     */
    public List<FoodMaterial> recognizeMixFood(String text, String image) {
        // 调用 AI API 混合识别食材
        // 这里使用模拟数据，实际项目中需要调用真实的 API
        List<FoodMaterial> foodList = new ArrayList<>();
        
        // 模拟识别结果
        FoodMaterial food1 = new FoodMaterial();
        food1.setName("西红柿");
        food1.setType("蔬菜");
        food1.setQuantity(200.0);
        food1.setShelfLife(7);
        foodList.add(food1);
        
        FoodMaterial food2 = new FoodMaterial();
        food2.setName("鸡蛋");
        food2.setType("蛋类");
        food2.setQuantity(2.0);
        food2.setShelfLife(30);
        foodList.add(food2);
        
        FoodMaterial food3 = new FoodMaterial();
        food3.setName("青菜");
        food3.setType("蔬菜");
        food3.setQuantity(100.0);
        food3.setShelfLife(3);
        foodList.add(food3);
        
        return foodList;
    }
    
    /**
     * 生成食谱
     * @param foodIds 食材ID列表
     * @param userId 用户ID
     * @param expectCount 期望食谱数量
     * @return 食谱列表
     */
    public List<Recipe> generateRecipe(List<Long> foodIds, Long userId, int expectCount) {
        // 调用 AI API 生成食谱
        // 这里使用模拟数据，实际项目中需要调用真实的 API
        List<Recipe> recipeList = new ArrayList<>();
        
        // 模拟生成结果
        Recipe recipe1 = new Recipe();
        recipe1.setName("西红柿炒鸡蛋");
        recipe1.setUserId(userId);
        recipe1.setCookingTime(15);
        recipe1.setDifficultyLevel("简单");
        recipe1.setSteps("1. 西红柿洗净切块，鸡蛋打散备用；2. 锅中倒油，油热后倒入鸡蛋翻炒至凝固盛出；3. 锅中留少许油，放入西红柿翻炒出汁，加入鸡蛋炒匀，加盐调味即可。");
        recipe1.setFoodIds(foodIds.toString());
        recipe1.setCollectCount(120);
        recipeList.add(recipe1);
        
        Recipe recipe2 = new Recipe();
        recipe2.setName("清炒青菜");
        recipe2.setUserId(userId);
        recipe2.setCookingTime(10);
        recipe2.setDifficultyLevel("简单");
        recipe2.setSteps("1. 青菜洗净切段；2. 锅中倒油，油热后放入青菜翻炒；3. 加入盐调味，翻炒均匀即可。");
        recipe2.setFoodIds(foodIds.toString());
        recipe2.setCollectCount(80);
        recipeList.add(recipe2);
        
        return recipeList;
    }
    
    /**
     * 分析营养成分
     * @param recipeId 食谱ID
     * @return 营养分析结果
     */
    public String analyzeNutrition(Long recipeId) {
        // 调用 AI API 分析营养成分
        // 这里使用模拟数据，实际项目中需要调用真实的 API
        return "{\"nutritionData\": {\"calorie\": 280.5,\"protein\": 15.2,\"fat\": 12.3,\"carbohydrate\": 25.6,\"vitamin\": \"维生素C：25mg，维生素E：3.2mg\",\"mineral\": \"铁：1.5mg，钙：80mg\"},\"evaluation\": \"该食谱热量符合减脂用户每日需求（1500大卡）的18.7%，蛋白质摄入达标，脂肪含量略低，可适当增加优质脂肪摄入\",\"suggestion\": \"可将50g青菜替换为1个鸡蛋，增加6g蛋白质与3g脂肪，使营养更均衡\"}";
    }
}