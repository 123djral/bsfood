package com.bsfood.recipegenerator.utils;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.bsfood.recipegenerator.entity.FoodMaterial;
import com.bsfood.recipegenerator.entity.Recipe;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.util.ArrayList;
import java.util.List;

/**
 * AI API 客户端 - 调用阿里云百炼平台
 */
@Component
public class AiApiClient {

    @Value("${ai.api-key}")
    private String apiKey;

    @Value("${ai.base-url}")
    private String baseUrl;

    private final HttpClient httpClient = HttpClient.newBuilder()
            .connectTimeout(Duration.ofSeconds(30))
            .build();

    /**
     * 调用百炼平台 Chat Completions API
     */
    private String callAiApi(String systemPrompt, String userPrompt) {
        JSONObject requestBody = new JSONObject();
        requestBody.put("model", "qwen-plus");
        requestBody.put("temperature", 0.7);

        JSONArray messages = new JSONArray();
        JSONObject systemMsg = new JSONObject();
        systemMsg.put("role", "system");
        systemMsg.put("content", systemPrompt);
        messages.add(systemMsg);

        JSONObject userMsg = new JSONObject();
        userMsg.put("role", "user");
        userMsg.put("content", userPrompt);
        messages.add(userMsg);

        requestBody.put("messages", messages);

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(baseUrl + "/chat/completions"))
                .header("Content-Type", "application/json")
                .header("Authorization", "Bearer " + apiKey)
                .timeout(Duration.ofSeconds(60))
                .POST(HttpRequest.BodyPublishers.ofString(requestBody.toJSONString()))
                .build();

        try {
            HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
            if (response.statusCode() == 200) {
                JSONObject respJson = JSON.parseObject(response.body());
                JSONArray choices = respJson.getJSONArray("choices");
                if (choices != null && !choices.isEmpty()) {
                    return choices.getJSONObject(0).getJSONObject("message").getString("content");
                }
            }
            throw new RuntimeException("AI API调用失败，状态码: " + response.statusCode() + "，响应: " + response.body());
        } catch (IOException | InterruptedException e) {
            throw new RuntimeException("AI API调用异常: " + e.getMessage(), e);
        }
    }

    /**
     * 从AI返回的文本中提取JSON内容
     */
    private String extractJson(String text) {
        // AI可能返回markdown代码块包裹的JSON
        if (text.contains("```json")) {
            int start = text.indexOf("```json") + 7;
            int end = text.indexOf("```", start);
            if (end > start) {
                return text.substring(start, end).trim();
            }
        }
        if (text.contains("```")) {
            int start = text.indexOf("```") + 3;
            int end = text.indexOf("```", start);
            if (end > start) {
                return text.substring(start, end).trim();
            }
        }
        // 尝试找到JSON数组或对象
        int arrStart = text.indexOf('[');
        int objStart = text.indexOf('{');
        if (arrStart >= 0 && (objStart < 0 || arrStart < objStart)) {
            int arrEnd = text.lastIndexOf(']');
            if (arrEnd > arrStart) return text.substring(arrStart, arrEnd + 1);
        }
        if (objStart >= 0) {
            int objEnd = text.lastIndexOf('}');
            if (objEnd > objStart) return text.substring(objStart, objEnd + 1);
        }
        return text.trim();
    }

    /**
     * 识别文本食材
     */
    public List<FoodMaterial> recognizeTextFood(String text) {
        String systemPrompt = "你是一个食材识别专家。用户会输入食材文本信息，请识别其中的食材名称、数量、类别。" +
                "请以JSON数组格式返回，每个元素包含：name(食材名称)、type(类别：蔬菜/肉类/蛋类/水产/豆制品/调味品/主食/水果/其他)、quantity(数量，数字)、shelfLife(保质期天数，整数)。" +
                "只返回JSON数组，不要其他文字。";
        String userPrompt = "请识别以下食材信息：" + text;

        String aiResponse = callAiApi(systemPrompt, userPrompt);
        return parseFoodListFromJson(extractJson(aiResponse));
    }

    /**
     * 识别图像食材（通过文字描述转发）
     */
    public List<FoodMaterial> recognizeImageFood(String imageDescription) {
        String systemPrompt = "你是一个食材识别专家。用户会描述一张食材图片的内容，请识别其中可能包含的食材。" +
                "请以JSON数组格式返回，每个元素包含：name(食材名称)、type(类别：蔬菜/肉类/蛋类/水产/豆制品/调味品/主食/水果/其他)、quantity(估计数量，数字，单位克)、shelfLife(保质期天数)。" +
                "只返回JSON数组，不要其他文字。";
        String userPrompt = "请根据以下描述识别食材：" + imageDescription;

        String aiResponse = callAiApi(systemPrompt, userPrompt);
        return parseFoodListFromJson(extractJson(aiResponse));
    }

    /**
     * 混合识别食材
     */
    public List<FoodMaterial> recognizeMixFood(String text, String imageDescription) {
        String systemPrompt = "你是一个食材识别专家。用户会同时提供文本和图片描述的食材信息，请综合识别所有食材。" +
                "请以JSON数组格式返回，每个元素包含：name(食材名称)、type(类别：蔬菜/肉类/蛋类/水产/豆制品/调味品/主食/水果/其他)、quantity(数量，数字)、shelfLife(保质期天数)。" +
                "去重合并相同食材，只返回JSON数组。";
        String userPrompt = "文本输入的食材：" + text + "\n图片描述的食材：" + imageDescription;

        String aiResponse = callAiApi(systemPrompt, userPrompt);
        return parseFoodListFromJson(extractJson(aiResponse));
    }

    /**
     * 生成食谱
     */
    public List<Recipe> generateRecipe(List<String> foodNames, Long userId, int expectCount,
                                       String difficultyLevel, String tastePreference, String cookingLevel, Integer maxCookingTime) {
        String systemPrompt = "你是一个专业厨师和食谱设计师。根据用户提供的食材，生成个性化食谱。" +
                "你可以在用户提供的食材基础上自动补充常见调味料和辅料。" +
                "请以JSON数组格式返回，每个食谱包含：name(食谱名称)、cookingTime(烹饪时间分钟数)、difficultyLevel(难度：简单/中等/困难)、steps(详细烹饪步骤，字符串，每步用序号开头)、foodIds(使用的食材名称列表，字符串)。" +
                "只返回JSON数组，不要其他文字。";

        StringBuilder userPrompt = new StringBuilder();
        userPrompt.append("现有食材：").append(String.join("、", foodNames)).append("\n");
        userPrompt.append("请生成").append(expectCount).append("个食谱。\n");
        if (difficultyLevel != null) {
            userPrompt.append("难度要求：").append(difficultyLevel).append("\n");
        }
        if (tastePreference != null) {
            userPrompt.append("口味偏好：").append(tastePreference).append("\n");
        }
        if (cookingLevel != null) {
            userPrompt.append("用户烹饪水平：").append(cookingLevel).append("，请根据水平调整步骤详细程度。\n");
        }
        if (maxCookingTime != null && maxCookingTime > 0) {
            userPrompt.append("期望烹饪时间不超过").append(maxCookingTime).append("分钟。\n");
        }

        String aiResponse = callAiApi(systemPrompt, userPrompt.toString());
        return parseRecipeListFromJson(extractJson(aiResponse), userId);
    }

    /**
     * 分析营养成分
     */
    public String analyzeNutrition(String recipeName, String recipeSteps, List<String> foodNames) {
        String systemPrompt = "你是一个营养学专家，精通《中国居民膳食指南》。请分析食谱的营养成分并给出评估。" +
                "请以JSON格式返回，包含：" +
                "nutritionData(对象，含calorie热量大卡、protein蛋白质g、fat脂肪g、carbohydrate碳水g、vitamin维生素描述、mineral矿物质描述)、" +
                "evaluation(营养评估文字，参考中国居民膳食指南)、" +
                "suggestion(改善建议文字)。" +
                "只返回JSON对象，不要其他文字。";

        String userPrompt = "食谱名称：" + recipeName + "\n" +
                "使用食材：" + String.join("、", foodNames) + "\n" +
                "烹饪步骤：" + recipeSteps;

        String aiResponse = callAiApi(systemPrompt, userPrompt);
        return extractJson(aiResponse);
    }

    /**
     * 推荐替代食材
     */
    public List<FoodMaterial> recommendSubstitute(String foodName, String foodType) {
        String systemPrompt = "你是一个食材专家。请为用户推荐可以替代指定食材的其他食材。" +
                "请以JSON数组格式返回，每个元素包含：name(食材名称)、type(类别)、quantity(建议用量克)、shelfLife(保质期天数)。" +
                "推荐3-5个替代食材，只返回JSON数组。";
        String userPrompt = "请推荐可以替代「" + foodName + "」（类别：" + foodType + "）的食材。";

        String aiResponse = callAiApi(systemPrompt, userPrompt);
        return parseFoodListFromJson(extractJson(aiResponse));
    }

    private List<FoodMaterial> parseFoodListFromJson(String json) {
        List<FoodMaterial> foodList = new ArrayList<>();
        try {
            JSONArray arr = JSON.parseArray(json);
            for (int i = 0; i < arr.size(); i++) {
                JSONObject obj = arr.getJSONObject(i);
                FoodMaterial food = new FoodMaterial();
                food.setName(obj.getString("name"));
                food.setType(obj.containsKey("type") ? obj.getString("type") : "其他");
                food.setQuantity(obj.containsKey("quantity") ? obj.getDouble("quantity") : 100.0);
                food.setShelfLife(obj.containsKey("shelfLife") ? obj.getInteger("shelfLife") : 7);
                foodList.add(food);
            }
        } catch (Exception e) {
            throw new RuntimeException("解析AI返回的食材数据失败: " + e.getMessage(), e);
        }
        return foodList;
    }

    private List<Recipe> parseRecipeListFromJson(String json, Long userId) {
        List<Recipe> recipeList = new ArrayList<>();
        try {
            JSONArray arr = JSON.parseArray(json);
            for (int i = 0; i < arr.size(); i++) {
                JSONObject obj = arr.getJSONObject(i);
                Recipe recipe = new Recipe();
                recipe.setName(obj.getString("name"));
                recipe.setUserId(userId);
                recipe.setCookingTime(obj.containsKey("cookingTime") ? obj.getInteger("cookingTime") : 30);
                recipe.setDifficultyLevel(obj.containsKey("difficultyLevel") ? obj.getString("difficultyLevel") : "中等");
                recipe.setSteps(obj.containsKey("steps") ? obj.getString("steps") : "");
                recipe.setFoodIds(obj.containsKey("foodIds") ? obj.getString("foodIds") : "");
                recipe.setCollectCount(0);
                recipeList.add(recipe);
            }
        } catch (Exception e) {
            throw new RuntimeException("解析AI返回的食谱数据失败: " + e.getMessage(), e);
        }
        return recipeList;
    }
}
