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

    @Value("${ai.dashscope-api-key:}")
    private String dashscopeApiKey;

    @Value("${ai.dashscope-base-url:}")
    private String dashscopeBaseUrl;

    private final HttpClient httpClient = HttpClient.newBuilder()
            .connectTimeout(Duration.ofSeconds(30))
            .build();

    /**
     * 调用 Chat Completions API（纯文本）
     */
    private String callAiApi(String systemPrompt, String userPrompt) {
        return callAiApi(systemPrompt, userPrompt, (String) null);
    }

    /**
     * 调用 Chat Completions API（支持图片理解）
     * @param systemPrompt 系统提示
     * @param userPrompt 文字提示
     * @param imageDataUrl 图片DataURL（为空则不使用图片）
     */
    private String callAiApi(String systemPrompt, String userPrompt, String imageDataUrl) {
        JSONObject requestBody = new JSONObject();
        requestBody.put("model", "MiniMax-M2.7");
        requestBody.put("temperature", 0.7);

        JSONArray messages = new JSONArray();

        // System message
        JSONObject systemMsg = new JSONObject();
        systemMsg.put("role", "system");
        systemMsg.put("content", systemPrompt);
        messages.add(systemMsg);

        // User message
        JSONObject userMsg = new JSONObject();
        userMsg.put("role", "user");

        if (imageDataUrl != null && !imageDataUrl.isEmpty()) {
            // Vision 多模态格式：content 为对象数组
            JSONArray contentArray = new JSONArray();
            JSONObject textPart = new JSONObject();
            textPart.put("type", "text");
            textPart.put("text", userPrompt);
            contentArray.add(textPart);

            JSONObject imagePart = new JSONObject();
            imagePart.put("type", "image_url");
            JSONObject imageUrl = new JSONObject();
            // MiniMax要求图片URL格式：data:image/xxx;base64,xxxxx，添加detail参数
            imageUrl.put("url", imageDataUrl);
            imageUrl.put("detail", "high");
            imagePart.put("image_url", imageUrl);
            contentArray.add(imagePart);

            userMsg.put("content", contentArray);
        } else {
            userMsg.put("content", userPrompt);
        }
        messages.add(userMsg);

        requestBody.put("messages", messages);

        String requestJson = requestBody.toJSONString();
        System.out.println(">>> 发送给MiniMax的请求体: " + requestJson);

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(baseUrl + "/chat/completions"))
                .header("Content-Type", "application/json")
                .header("Authorization", "Bearer " + apiKey)
                .timeout(Duration.ofSeconds(180))
                .POST(HttpRequest.BodyPublishers.ofString(requestJson))
                .build();

        try {
            HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
            String responseBody = response.body();
            System.out.println(">>> MiniMax响应状态: " + response.statusCode());
            System.out.println(">>> MiniMax响应内容: " + (responseBody != null && responseBody.length() > 500 ? responseBody.substring(0, 500) + "..." : responseBody));
            if (response.statusCode() == 200) {
                JSONObject respJson = JSON.parseObject(responseBody);
                JSONArray choices = respJson.getJSONArray("choices");
                if (choices != null && !choices.isEmpty()) {
                    String content = choices.getJSONObject(0).getJSONObject("message").getString("content");
                    System.out.println(">>> AI返回内容: " + (content != null && content.length() > 200 ? content.substring(0, 200) + "..." : content));
                    return content;
                }
            }
            throw new RuntimeException("AI API调用失败，状态码: " + response.statusCode() + "，响应: " + responseBody);
        } catch (IOException | InterruptedException e) {
            throw new RuntimeException("AI API调用异常: " + e.getMessage(), e);
        }
    }

    /**
     * 从AI返回的文本中提取JSON内容
     */
    private String extractJson(String text) {
        System.out.println(">>> extractJson收到内容: " + (text != null && text.length() > 500 ? text.substring(0, 500) + "..." : text));
        if (text == null || text.isEmpty()) {
            throw new RuntimeException("AI返回内容为空");
        }
        // 去除UTF-8 BOM
        if (text.startsWith("\uFEFF")) {
            text = text.substring(1);
        }
        text = text.trim();

        // 0. 去除UTF-8 BOM
        if (text.startsWith("\uFEFF")) {
            text = text.substring(1);
        }

        // 1. AI可能返回markdown代码块包裹的JSON（优先检查）
        if (text.contains("```json")) {
            int start = text.indexOf("```json") + 7;
            int end = text.lastIndexOf("```");
            if (end > start) {
                String extracted = text.substring(start, end).trim();
                if (isValidJsonObject(extracted)) {
                    return extracted;
                }
                if (isValidJsonArray(extracted)) {
                    return extracted;
                }
            }
        }
        if (text.contains("```")) {
            int start = text.indexOf("```") + 3;
            int end = text.lastIndexOf("```");
            if (end > start) {
                String extracted = text.substring(start, end).trim();
                if (isValidJsonObject(extracted)) {
                    return extracted;
                }
                if (isValidJsonArray(extracted)) {
                    return extracted;
                }
            }
        }

        // 2. 尝试找JSON对象（优先于数组，因为营养分析返回的是对象）
        int objStart = text.indexOf('{');
        if (objStart >= 0) {
            int objEnd = text.lastIndexOf('}');
            if (objEnd > objStart) {
                String potentialJson = text.substring(objStart, objEnd + 1);
                if (isValidJsonObject(potentialJson)) {
                    return potentialJson;
                }
            }
        }

        // 3. 尝试找JSON数组
        int arrStart = text.indexOf('[');
        if (arrStart >= 0) {
            int arrEnd = text.lastIndexOf(']');
            if (arrEnd > arrStart) {
                String potentialJson = text.substring(arrStart, arrEnd + 1);
                if (isValidJsonArray(potentialJson)) {
                    return potentialJson;
                }
            }
        }

        // 没有找到任何JSON结构
        throw new RuntimeException("AI返回内容不是JSON格式，内容为：" + text);
    }

    private boolean isValidJsonArray(String text) {
        if (text == null || text.trim().isEmpty()) return false;
        try {
            JSON.parseArray(text);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    private boolean isValidJsonObject(String text) {
        if (text == null || text.trim().isEmpty()) return false;
        try {
            JSON.parseObject(text);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    /**
     * 识别文本食材
     */
    public List<FoodMaterial> recognizeTextFood(String text) {
        String systemPrompt = "你是一个食材识别专家。用户会输入食材文本信息，请识别其中的食材名称，并根据文本描述估算食材的数量和保质期。" +
                "估算规则：数量单位为克(g)，根据食材名称和文本描述的份量估算，如'两个番茄'约200g，'一把青菜'约150g；保质期单位为天，根据食材类型估算，新鲜蔬菜2-5天，肉类1-3天，鸡蛋7-14天等。" +
                "请以JSON数组格式返回，每个元素包含：name(食材名称)、type(类别：蔬菜/肉类/蛋类/水产/豆制品/调味品/主食/水果/其他)、quantity(数量，数字，单位g)、shelfLife(保质期天数，整数)。" +
                "只返回JSON数组，不要其他文字。";
        String userPrompt = "请识别以下食材信息：" + text;

        String aiResponse = callAiApi(systemPrompt, userPrompt);
        return parseFoodListFromJson(extractJson(aiResponse));
    }

    /**
     * 识别图像食材（直接传图片）
     * 使用MiniMax专用图像理解API
     */
    public List<FoodMaterial> recognizeImageFood(String imageDataUrl) {
        String userPrompt = "请仔细观察这张图片，识别其中的食材，并估算每种食材的份量和新鲜程度。" +
                "估算规则：" +
                "1. quantity(数量，单位g)：根据图片中食材的可见份量估算，如一个苹果约150-200g，一把青菜约150-200g，一块肉约200-300g等" +
                "2. shelfLife(保质期，单位天)：根据食材类型和新鲜程度估算，新鲜蔬菜2-5天，肉类1-3天，鸡蛋7-14天，调味品30天以上等" +
                "请以JSON数组格式返回，每个元素包含：name(食材名称)、type(类别：蔬菜/肉类/蛋类/水产/豆制品/调味品/主食/水果/其他)、quantity(数量，数字，单位g)、shelfLife(保质期天数，整数)。" +
                "只返回JSON数组，不要其他文字。";

        String aiResponse = callImageUnderstandApi(userPrompt, imageDataUrl);
        return parseFoodListFromJson(extractJson(aiResponse));
    }

    /**
     * 混合识别食材
     */
    public List<FoodMaterial> recognizeMixFood(String text, String imageDataUrl) {
        String userPrompt = "文本输入的食材：" + text + "\n请结合图片识别更多食材，并根据图片估算每种食材的份量和保质期。" +
                "数量单位为克(g)，根据图片中食材的份量合理估算。" +
                "只返回JSON数组，不要其他文字。";

        String aiResponse = callImageUnderstandApi(userPrompt, imageDataUrl);
        return parseFoodListFromJson(extractJson(aiResponse));
    }

    /**
     * 调用图像理解API（优先使用Qwen-VL，其次尝试MiniMax）
     */
    private String callImageUnderstandApi(String prompt, String imageDataUrl) {
        // 优先使用Qwen-VL（如果配置了DashScope API Key）
        if (dashscopeApiKey != null && !dashscopeApiKey.isEmpty() && !dashscopeApiKey.startsWith("your-")) {
            return callQwenVlImageUnderstand(prompt, imageDataUrl);
        }

        // 回退到MiniMax（但MiniMax图像理解API可能不可用）
        return callMiniMaxImageUnderstand(prompt, imageDataUrl);
    }

    /**
     * 使用 Qwen-VL 进行图像理解（阿里云百炼）
     */
    private String callQwenVlImageUnderstand(String prompt, String imageDataUrl) {
        JSONObject requestBody = new JSONObject();
        requestBody.put("model", "qwen3-vl-plus");

        JSONArray messages = new JSONArray();

        JSONObject userMsg = new JSONObject();
        userMsg.put("role", "user");

        // 使用正确的格式：图片作为对象，文本作为另一个对象
        JSONArray contentArray = new JSONArray();

        // 图片部分 - 使用正确的格式
        JSONObject imagePart = new JSONObject();
        imagePart.put("type", "image_url");
        JSONObject imageUrlObj = new JSONObject();
        imageUrlObj.put("url", imageDataUrl);
        imagePart.put("image_url", imageUrlObj);
        contentArray.add(imagePart);

        // 确保prompt明确要求返回JSON
        String jsonPrompt = prompt + "\n请只返回JSON数组格式，不要其他文字。格式：[{name:食材名称, type:类别}]";
        JSONObject textPart = new JSONObject();
        textPart.put("type", "text");
        textPart.put("text", jsonPrompt);
        contentArray.add(textPart);

        userMsg.put("content", contentArray);
        messages.add(userMsg);

        requestBody.put("messages", messages);

        String requestJson = requestBody.toJSONString();
        System.out.println(">>> 发送给Qwen-VL图像理解的请求体: " + (requestJson.length() > 500 ? requestJson.substring(0, 500) + "..." : requestJson));

        String targetBaseUrl = dashscopeBaseUrl != null && !dashscopeBaseUrl.isEmpty()
            ? dashscopeBaseUrl : "https://dashscope.aliyuncs.com/compatible-mode/v1";

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(targetBaseUrl + "/chat/completions"))
                .header("Content-Type", "application/json")
                .header("Authorization", "Bearer " + dashscopeApiKey)
                .timeout(Duration.ofSeconds(180))
                .POST(HttpRequest.BodyPublishers.ofString(requestJson))
                .build();

        try {
            HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
            String responseBody = response.body();
            System.out.println(">>> Qwen-VL图像理解响应状态: " + response.statusCode());
            System.out.println(">>> Qwen-VL图像理解响应内容: " + (responseBody != null && responseBody.length() > 500 ? responseBody.substring(0, 500) + "..." : responseBody));
            if (response.statusCode() == 200) {
                JSONObject respJson = JSON.parseObject(responseBody);
                JSONArray choices = respJson.getJSONArray("choices");
                if (choices != null && !choices.isEmpty()) {
                    String content = choices.getJSONObject(0).getJSONObject("message").getString("content");
                    System.out.println(">>> AI返回内容: " + (content != null && content.length() > 200 ? content.substring(0, 200) + "..." : content));
                    return content;
                }
            }
            throw new RuntimeException("Qwen-VL图像理解API调用失败，状态码: " + response.statusCode() + "，响应: " + responseBody);
        }
        catch (IOException | InterruptedException e) {
            throw new RuntimeException("Qwen-VL图像理解API调用异常: " + e.getMessage(), e);
        }
    }

    /**
     * 使用 MiniMax 进行图像理解（已弃用，API可能不可用）
     */
    private String callMiniMaxImageUnderstand(String prompt, String imageDataUrl) {
        System.out.println(">>> 警告: MiniMax图像理解API可能不可用，建议配置Qwen-VL");

        JSONObject requestBody = new JSONObject();
        requestBody.put("model", "MiniMax-M2.7");

        JSONArray messages = new JSONArray();

        JSONObject userMsg = new JSONObject();
        userMsg.put("role", "user");

        JSONArray contentArray = new JSONArray();
        JSONObject textPart = new JSONObject();
        textPart.put("type", "text");
        textPart.put("text", prompt);
        contentArray.add(textPart);

        JSONObject imagePart = new JSONObject();
        imagePart.put("type", "image_url");
        JSONObject imageUrl = new JSONObject();
        imageUrl.put("url", imageDataUrl);
        imageUrl.put("detail", "high");
        imagePart.put("image_url", imageUrl);
        contentArray.add(imagePart);

        userMsg.put("content", contentArray);
        messages.add(userMsg);

        requestBody.put("messages", messages);

        String requestJson = requestBody.toJSONString();
        System.out.println(">>> 发送给MiniMax图像理解的请求体: " + requestJson);

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(baseUrl + "/image_understand"))
                .header("Content-Type", "application/json")
                .header("Authorization", "Bearer " + apiKey)
                .timeout(Duration.ofSeconds(180))
                .POST(HttpRequest.BodyPublishers.ofString(requestJson))
                .build();

        try {
            HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
            String responseBody = response.body();
            System.out.println(">>> MiniMax图像理解响应状态: " + response.statusCode());
            System.out.println(">>> MiniMax图像理解响应内容: " + (responseBody != null && responseBody.length() > 500 ? responseBody.substring(0, 500) + "..." : responseBody));
            if (response.statusCode() == 200) {
                JSONObject respJson = JSON.parseObject(responseBody);
                JSONArray choices = respJson.getJSONArray("choices");
                if (choices != null && !choices.isEmpty()) {
                    String content = choices.getJSONObject(0).getJSONObject("message").getString("content");
                    System.out.println(">>> AI返回内容: " + (content != null && content.length() > 200 ? content.substring(0, 200) + "..." : content));
                    return content;
                }
            }
            throw new RuntimeException("图像理解API调用失败，状态码: " + response.statusCode() + "，响应: " + responseBody);
        }
        catch (IOException | InterruptedException e) {
            throw new RuntimeException("图像理解API调用异常: " + e.getMessage(), e);
        }
    }

    /**
     * 生成食谱
     */
    public List<Recipe> generateRecipe(List<String> foodNames, Long userId, int expectCount,
                                       String difficultyLevel, String tastePreference, String cookingLevel, Integer maxCookingTime,
                                       String dietLimit, String healthGoal) {
        StringBuilder systemPrompt = new StringBuilder();
        systemPrompt.append("你是一个专业厨师和食谱设计师。根据用户提供的食材，生成详细的个性化食谱。");
        systemPrompt.append("你可以在用户提供的食材基础上自动补充常见调味料和辅料。");
        systemPrompt.append("请以JSON数组格式返回，每个食谱包含非常详细的信息：");
        systemPrompt.append("name(中文食谱名称)、englishName(英文食谱名称)、imageKeyword(图片搜索关键词，简洁英文如'kung pao chicken')、");
        systemPrompt.append("cookingTime(烹饪时间分钟数)、difficultyLevel(难度：简单/中等/困难)、servings(几人份，人数)、");
        systemPrompt.append("cuisineStyle(菜系，如川菜、粤菜、湘菜、鲁菜、苏菜、浙菜、闽菜、徽菜、家常菜等)、");
        systemPrompt.append("flavorProfile(口味特点，如麻辣、鲜香、咸鲜、甜酸、清淡等)、");
        systemPrompt.append("suitableCrowd(适宜人群，如上班族、儿童、老年人、孕妇、减肥人群等)、");
        systemPrompt.append("ingredients(详细食材列表，JSON数组，每个元素包含name食材名称和quantity用量，如{name:\"鸡腿肉\",quantity:\"200g\"})、");
        systemPrompt.append("tools(所需厨具工具列表，JSON数组，如{name:\"炒锅\"}、{name:\"砧板\"}等)、");
        systemPrompt.append("steps(非常详细的烹饪步骤，字符串，每步用序号开头，每步要说明具体操作和火候/时间)、");
        systemPrompt.append("tips(烹饪小贴士，字符串，分享让菜品更好吃的技巧)、");
        systemPrompt.append("nutritionBrief(简要营养信息，每100g的热量大卡数和主要营养特点描述)、");
        systemPrompt.append("foodIds(使用的食材名称列表，字符串，逗号分隔，用于系统关联)。");
        systemPrompt.append("重要：steps步骤必须非常详细，每步至少30字以上，包含具体的食材处理、刀工、火候、调味等细节；tips至少包含2-3条实用技巧；ingredients要列出所有用到的食材及用量。");
        systemPrompt.append("只返回JSON数组，不要其他文字。");

        StringBuilder userPrompt = new StringBuilder();
        userPrompt.append("现有食材：").append(String.join("、", foodNames)).append("\n");
        userPrompt.append("请生成").append(expectCount).append("个详细食谱。\n");

        // 添加健康目标
        if (healthGoal != null && !healthGoal.isEmpty()) {
            userPrompt.append("健康目标：").append(healthGoal).append("，请生成符合该目标的营养均衡食谱。\n");
        }

        // 添加饮食限制
        if (dietLimit != null && !dietLimit.isEmpty()) {
            userPrompt.append("饮食限制/过敏原：").append(dietLimit).append("，生成的食谱绝对不能包含这些食材或与其相关的食材。\n");
        }

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

        String aiResponse = callAiApi(systemPrompt.toString(), userPrompt.toString());
        return parseRecipeListFromJson(extractJson(aiResponse), userId);
    }

    /**
     * 分析营养成分
     */
    public String analyzeNutrition(String recipeName, String recipeSteps, List<String> foodNames) {
        String systemPrompt = "你是一个资深营养学专家，精通《中国居民膳食指南》和中式烹饪营养学。请对这个食谱进行非常详细的营养分析。" +
                "请以JSON格式返回，包含以下详细信息：" +
                "1. nutritionData(详细营养成分对象)：\n" +
                "   - calorie(总热量大卡)\n" +
                "   - caloriePerHundred(每100克热量大卡)\n" +
                "   - protein(蛋白质g)\n" +
                "   - fat(脂肪g)\n" +
                "   - saturatedFat(饱和脂肪酸g)\n" +
                "   - unsaturatedFat(不饱和脂肪酸g)\n" +
                "   - carbohydrate(碳水化合物g)\n" +
                "   - dietaryFiber(膳食纤维g)\n" +
                "   - sodium(钠mg)\n" +
                "   - cholesterol(胆固醇mg)\n" +
                "   - vitaminA(维生素A微克)\n" +
                "   - vitaminC(维生素C毫克)\n" +
                "   - vitaminE(维生素E毫克)\n" +
                "   - calcium(钙mg)\n" +
                "   - iron(铁mg)\n" +
                "   - zinc(锌mg)\n" +
                "   - selenium(硒微克)\n" +
                "2. dailyValuePercentages(每日推荐值百分比对象，对应成人每日所需)：\n" +
                "   - caloriePct(热量百分比，如15表示占每日推荐的15%)\n" +
                "   - proteinPct(蛋白质百分比)\n" +
                "   - fatPct(脂肪百分比)\n" +
                "   - carbohydratePct(碳水百分比)\n" +
                "   - sodiumPct(钠百分比)\n" +
                "   - fiberPct(膳食纤维百分比)\n" +
                "3. nutritionBalance(营养均衡性评估对象)：\n" +
                "   - macroBalance(宏量营养素均衡评分0-100，评估蛋白质/脂肪/碳水比例是否合理)\n" +
                "   - microBalance(微量营养素均衡评分0-100，评估维生素矿物质丰富度)\n" +
                "   - sodiumLevel(钠含量水平：低/中/高)\n" +
                "   - overallScore(综合营养评分0-100)\n" +
                "4. evaluation(详细营养评估，300字以上，基于中国居民膳食指南进行评估，包含：总热量是否合适、宏量营养素比例是否合理、微量营养素是否丰富、是否符合食物多样性原则、是否适合目标人群)\n" +
                "5. suggestion(详细改善建议，200字以上，包含：如何优化这道菜的营养、可以替换哪些食材更健康、烹饪方式有什么改进建议、适合搭配什么其他菜肴达到膳食平衡、特殊人群如老人儿童孕妇的调整建议)\n" +
                "6. suitableCrowd(适宜人群数组，如[\"健康人群\",\"上班族\",\"贫血人群\"])\n" +
                "7. contraindications(禁忌人群数组，如[\"痛风患者\",\"海鲜过敏者\",\"肾病患者\"])\n" +
                "8. mealAdvice(膳食搭配建议，字符串，150字以上，建议这道菜适合与什么主食、其他菜肴、果蔬搭配，形成完整的一餐)\n" +
                "只返回JSON对象，不要其他文字。";

        String userPrompt = "食谱名称：" + recipeName + "\n" +
                "使用食材：" + String.join("、", foodNames) + "\n" +
                "烹饪步骤：" + recipeSteps;

        String aiResponse = callAiApi(systemPrompt, userPrompt);
        return extractJson(aiResponse);
    }

    /**
     * 使用 MiniMax image-01 模型生成食谱图片
     * @param recipeName 食谱名称（中文）
     * @param englishName 英文名称
     * @param prompt 英文图片描述 prompt
     * @return 生成的图片URL
     */
    public String generateRecipeImage(String recipeName, String englishName, String prompt) {
        JSONObject requestBody = new JSONObject();
        requestBody.put("model", "image-01");
        // 构建图片生成prompt：生成一张美食图片，真实照片风格
        String imagePrompt = String.format(
            "A professional food photography of %s, Chinese dish, appetizing presentation, restaurant style, top-down angle, natural lighting, 4K quality, the dish name is %s in Chinese",
            englishName != null && !englishName.isEmpty() ? englishName : recipeName,
            recipeName
        );
        requestBody.put("prompt", imagePrompt);
        requestBody.put("aspect_ratio", "1:1");
        requestBody.put("response_format", "url");
        requestBody.put("n", 1);
        requestBody.put("prompt_optimizer", true);

        String requestJson = requestBody.toJSONString();
        System.out.println(">>> 发送图片生成请求: " + imagePrompt);

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(baseUrl + "/image_generation"))
                .header("Content-Type", "application/json")
                .header("Authorization", "Bearer " + apiKey)
                .timeout(Duration.ofSeconds(120))
                .POST(HttpRequest.BodyPublishers.ofString(requestJson))
                .build();

        try {
            HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
            String responseBody = response.body();
            System.out.println(">>> 图片生成响应状态: " + response.statusCode());
            System.out.println(">>> 图片生成响应内容: " + (responseBody != null && responseBody.length() > 500 ? responseBody.substring(0, 500) : responseBody));

            if (response.statusCode() == 200) {
                JSONObject respJson = JSON.parseObject(responseBody);
                JSONObject baseResp = respJson.getJSONObject("base_resp");
                if (baseResp != null && baseResp.getInteger("status_code") != 0) {
                    throw new RuntimeException("图片生成失败: " + baseResp.getString("status_msg"));
                }
                JSONObject data = respJson.getJSONObject("data");
                if (data != null && data.containsKey("image_urls")) {
                    JSONArray imageUrls = data.getJSONArray("image_urls");
                    if (imageUrls != null && !imageUrls.isEmpty()) {
                        return imageUrls.getString(0);
                    }
                }
                throw new RuntimeException("图片生成返回数据格式异常");
            }
            throw new RuntimeException("图片生成API调用失败，状态码: " + response.statusCode());
        } catch (IOException | InterruptedException e) {
            throw new RuntimeException("图片生成异常: " + e.getMessage(), e);
        }
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
        if (json == null || json.isEmpty()) {
            throw new RuntimeException("AI返回的JSON内容为空");
        }
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
        if (json == null || json.trim().isEmpty()) {
            throw new RuntimeException("AI返回内容为空");
        }
        try {
            JSONArray arr = JSON.parseArray(json);
            for (int i = 0; i < arr.size(); i++) {
                try {
                    JSONObject obj = arr.getJSONObject(i);
                    Recipe recipe = new Recipe();
                    recipe.setName(obj.getString("name"));
                    recipe.setEnglishName(obj.containsKey("englishName") && obj.getString("englishName") != null ? obj.getString("englishName") : obj.getString("name"));
                    // imageKeyword用于Spoonacular图片搜索，优先级最高
                    if (obj.containsKey("imageKeyword") && obj.getString("imageKeyword") != null && !obj.getString("imageKeyword").isEmpty()) {
                        recipe.setImageKeyword(obj.getString("imageKeyword"));
                    } else {
                        recipe.setImageKeyword(obj.getString("englishName"));
                    }
                    recipe.setUserId(userId);
                    recipe.setCookingTime(obj.containsKey("cookingTime") && obj.get("cookingTime") != null ? obj.getInteger("cookingTime") : 30);
                    recipe.setDifficultyLevel(obj.containsKey("difficultyLevel") && obj.getString("difficultyLevel") != null ? obj.getString("difficultyLevel") : "中等");
                    recipe.setSteps(obj.containsKey("steps") && obj.getString("steps") != null ? obj.getString("steps") : "");
                    recipe.setFoodIds(obj.containsKey("foodIds") && obj.getString("foodIds") != null ? obj.getString("foodIds") : "");
                    recipe.setCollectCount(0);
                    // 新增详细字段
                    recipe.setServings(obj.containsKey("servings") && obj.get("servings") != null ? obj.getInteger("servings") : 2);
                    recipe.setCuisineStyle(obj.containsKey("cuisineStyle") && obj.getString("cuisineStyle") != null ? obj.getString("cuisineStyle") : "家常菜");
                    recipe.setFlavorProfile(obj.containsKey("flavorProfile") && obj.getString("flavorProfile") != null ? obj.getString("flavorProfile") : "");
                    recipe.setSuitableCrowd(obj.containsKey("suitableCrowd") && obj.get("suitableCrowd") != null ? obj.getString("suitableCrowd") : "");
                    // ingredients和tools转为JSON字符串存储
                    if (obj.containsKey("ingredients") && obj.get("ingredients") != null) {
                        recipe.setIngredients(obj.getJSONArray("ingredients").toJSONString());
                    }
                    if (obj.containsKey("tools") && obj.get("tools") != null) {
                        recipe.setTools(obj.getJSONArray("tools").toJSONString());
                    }
                    recipe.setTips(obj.containsKey("tips") && obj.getString("tips") != null ? obj.getString("tips") : "");
                    recipe.setNutritionBrief(obj.containsKey("nutritionBrief") && obj.getString("nutritionBrief") != null ? obj.getString("nutritionBrief") : "");
                    recipeList.add(recipe);
                } catch (Exception e) {
                    System.out.println(">>> 解析单个食谱失败，跳过: " + e.getMessage());
                    continue;
                }
            }
        } catch (Exception e) {
            System.out.println(">>> JSON解析失败，尝试修复: " + e.getMessage());
            // 尝试修复不完整的JSON
            String fixedJson = fixMalformedJson(json);
            if (fixedJson != null) {
                try {
                    JSONArray arr = JSON.parseArray(fixedJson);
                    for (int i = 0; i < arr.size(); i++) {
                        try {
                            JSONObject obj = arr.getJSONObject(i);
                            Recipe recipe = new Recipe();
                            recipe.setName(obj.getString("name"));
                            recipe.setEnglishName(obj.containsKey("englishName") && obj.getString("englishName") != null ? obj.getString("englishName") : obj.getString("name"));
                            recipe.setUserId(userId);
                            recipe.setCookingTime(obj.containsKey("cookingTime") && obj.get("cookingTime") != null ? obj.getInteger("cookingTime") : 30);
                            recipe.setDifficultyLevel(obj.containsKey("difficultyLevel") && obj.getString("difficultyLevel") != null ? obj.getString("difficultyLevel") : "中等");
                            recipe.setSteps(obj.containsKey("steps") && obj.getString("steps") != null ? obj.getString("steps") : "");
                            recipe.setFoodIds(obj.containsKey("foodIds") && obj.getString("foodIds") != null ? obj.getString("foodIds") : "");
                            recipe.setCollectCount(0);
                            recipe.setServings(obj.containsKey("servings") && obj.get("servings") != null ? obj.getInteger("servings") : 2);
                            recipe.setCuisineStyle(obj.containsKey("cuisineStyle") && obj.getString("cuisineStyle") != null ? obj.getString("cuisineStyle") : "家常菜");
                            recipe.setFlavorProfile(obj.containsKey("flavorProfile") && obj.getString("flavorProfile") != null ? obj.getString("flavorProfile") : "");
                            recipe.setSuitableCrowd(obj.containsKey("suitableCrowd") && obj.get("suitableCrowd") != null ? obj.getString("suitableCrowd") : "");
                            if (obj.containsKey("ingredients") && obj.get("ingredients") != null) {
                                recipe.setIngredients(obj.getJSONArray("ingredients").toJSONString());
                            }
                            if (obj.containsKey("tools") && obj.get("tools") != null) {
                                recipe.setTools(obj.getJSONArray("tools").toJSONString());
                            }
                            recipe.setTips(obj.containsKey("tips") && obj.getString("tips") != null ? obj.getString("tips") : "");
                            recipe.setNutritionBrief(obj.containsKey("nutritionBrief") && obj.getString("nutritionBrief") != null ? obj.getString("nutritionBrief") : "");
                            recipeList.add(recipe);
                        } catch (Exception ex) {
                            continue;
                        }
                    }
                } catch (Exception ex2) {
                    throw new RuntimeException("解析AI返回的食谱数据失败: " + ex2.getMessage() + "，原始内容: " + json);
                }
            } else {
                throw new RuntimeException("解析AI返回的食谱数据失败: " + e.getMessage() + "，原始内容: " + json);
            }
        }
        if (recipeList.isEmpty()) {
            throw new RuntimeException("未能解析出有效的食谱数据");
        }
        return recipeList;
    }

    /**
     * 尝试修复不完整的JSON
     */
    private String fixMalformedJson(String json) {
        if (json == null || json.trim().isEmpty()) {
            return null;
        }

        // 找到JSON数组的开始和结束位置
        int startIdx = -1;
        int endIdx = -1;

        // 从后往前找 ] 确保找到的是最后一个数组的结束
        for (int i = json.length() - 1; i >= 0; i--) {
            if (json.charAt(i) == ']') {
                endIdx = i;
                break;
            }
        }

        // 从找到的结束位置往前找对应的 [
        if (endIdx > 0) {
            int bracketCount = 0;
            for (int i = endIdx; i >= 0; i--) {
                if (json.charAt(i) == ']') {
                    bracketCount++;
                } else if (json.charAt(i) == '[') {
                    bracketCount--;
                    if (bracketCount == 0) {
                        startIdx = i;
                        break;
                    }
                }
            }
        }

        if (startIdx >= 0 && endIdx > startIdx) {
            String extracted = json.substring(startIdx, endIdx + 1);
            try {
                JSON.parseArray(extracted);
                return extracted;
            } catch (Exception e) {
                // 尝试修复常见的JSON问题
                String fixed = fixJsonString(extracted);
                try {
                    JSON.parseArray(fixed);
                    return fixed;
                } catch (Exception ex) {
                    return null;
                }
            }
        }

        return null;
    }

    /**
     * 修复常见的JSON字符串问题
     */
    private String fixJsonString(String json) {
        if (json == null || json.isEmpty()) return json;

        StringBuilder sb = new StringBuilder();
        boolean inString = false;
        char prevChar = 0;

        for (int i = 0; i < json.length(); i++) {
            char c = json.charAt(i);

            if (c == '"' && prevChar != '\\') {
                inString = !inString;
                sb.append(c);
            } else if (inString) {
                sb.append(c);
            } else if (c == '\'' ) {
                // 单引号改为双引号（常见错误）
                sb.append('"');
            } else if (c == '\n' || c == '\r' || c == '\t') {
                // 保留换行符在字符串内的情况
                sb.append(c);
            } else {
                sb.append(c);
            }

            prevChar = c;
        }

        return sb.toString();
    }
}
