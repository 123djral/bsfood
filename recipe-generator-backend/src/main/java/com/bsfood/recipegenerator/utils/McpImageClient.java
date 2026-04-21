package com.bsfood.recipegenerator.utils;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.bsfood.recipegenerator.entity.FoodMaterial;
import org.springframework.stereotype.Component;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * 通过 Claude Code CLI 调用 MCP 图像理解
 * 实际上调用的是 Claude Code 内置的 MiniMax MCP understand_image 工具
 */
@Component
public class McpImageClient {

    private static final String PYTHON_SCRIPT = "C:/develop/codeBase/bsfood1/recipe-generator-backend/scripts/claude_image_client.py";

    /**
     * 通过 Claude Code CLI 调用图像理解
     * @param imageDataUrl 图片的 Base64 DataURL 或 图片URL
     * @return 识别的食材列表
     */
    public List<FoodMaterial> recognizeImageViaMcp(String imageDataUrl) {
        List<FoodMaterial> foodList = new ArrayList<>();

        try {
            // 调用 Python 脚本，通过 Claude CLI 调用图像理解
            String result = callClaudeCli(imageDataUrl);
            if (result != null && !result.isEmpty()) {
                foodList = parseClaudeResult(result);
            }
        } catch (Exception e) {
            System.err.println("Claude CLI 图像识别失败: " + e.getMessage());
            e.printStackTrace();
        }

        return foodList;
    }

    /**
     * 调用 Python 脚本，通过 Claude CLI 进行图像理解（带重试）
     */
    private String callClaudeCli(String imageDataUrl) throws Exception {
        int maxRetries = 3;
        int retryDelaySeconds = 5;
        Exception lastException = null;

        for (int attempt = 1; attempt <= maxRetries; attempt++) {
            try {
                return callClaudeCliOnce(imageDataUrl);
            } catch (Exception e) {
                lastException = e;
                String errorMsg = e.getMessage();
                System.err.println("Claude CLI 调用失败 (尝试 " + attempt + "/" + maxRetries + "): " + errorMsg);

                // 如果是超时错误，尝试重试
                if (errorMsg != null && errorMsg.contains("超时")) {
                    if (attempt < maxRetries) {
                        System.out.println("等待 " + retryDelaySeconds + " 秒后重试...");
                        Thread.sleep(retryDelaySeconds * 1000);
                        retryDelaySeconds *= 2; // 指数退避
                    }
                } else {
                    // 非超时错误，不再重试
                    throw e;
                }
            }
        }
        throw lastException != null ? lastException : new Exception("Claude CLI 调用失败");
    }

    /**
     * 单次调用 Claude CLI
     */
    private String callClaudeCliOnce(String imageDataUrl) throws Exception {
        List<String> command = new ArrayList<>();
        command.add("python");
        command.add(PYTHON_SCRIPT);
        command.add(imageDataUrl);

        ProcessBuilder processBuilder = new ProcessBuilder(command);
        processBuilder.redirectErrorStream(true);

        Process process = processBuilder.start();

        // 读取输出
        StringBuilder output = new StringBuilder();
        try (BufferedReader reader = new BufferedReader(
                new InputStreamReader(process.getInputStream(), StandardCharsets.UTF_8))) {
            String line;
            while ((line = reader.readLine()) != null) {
                output.append(line);
            }
        }

        // 等待进程完成（最多120秒）
        boolean finished = process.waitFor(120, TimeUnit.SECONDS);
        if (!finished) {
            process.destroyForcibly();
            throw new Exception("Claude CLI 调用超时（超过120秒）");
        }

        return output.toString();
    }

    /**
     * 解析 Claude CLI 返回结果
     */
    private List<FoodMaterial> parseClaudeResult(String result) {
        List<FoodMaterial> foodList = new ArrayList<>();
        try {
            JSONObject json = JSON.parseObject(result);

            // 检查是否有错误
            if (json.containsKey("error")) {
                System.err.println("Claude CLI 调用错误: " + json.getString("error"));
                if (json.containsKey("raw")) {
                    System.err.println("原始响应: " + json.getString("raw"));
                }
                return foodList;
            }

            if (json.containsKey("success") && json.getBoolean("success")) {
                // data 可能是 JSON 字符串或直接是数组
                Object data = json.get("data");
                if (data != null) {
                    if (data instanceof JSONArray) {
                        foodList = parseFoodListFromJson(data.toString());
                    } else if (data instanceof String) {
                        String text = (String) data;
                        // 如果是字符串，可能是 JSON 数组格式
                        if (text.startsWith("[")) {
                            foodList = parseFoodListFromJson(text);
                        } else {
                            // 尝试解析
                            foodList = parseFoodListFromJson(extractJsonArray(text));
                        }
                    }
                }
            }
        } catch (Exception e) {
            System.err.println("解析 Claude 结果失败: " + e.getMessage());
            e.printStackTrace();
        }
        return foodList;
    }

    private List<FoodMaterial> parseFoodListFromJson(String json) {
        List<FoodMaterial> foodList = new ArrayList<>();
        if (json == null || json.isEmpty()) {
            return foodList;
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
            System.err.println("解析食材列表失败: " + e.getMessage());
        }
        return foodList;
    }

    private String extractJsonArray(String text) {
        if (text == null || text.isEmpty()) {
            return "[]";
        }
        text = text.trim();
        // 去除 markdown 代码块
        if (text.contains("```json")) {
            int start = text.indexOf("```json") + 7;
            int end = text.lastIndexOf("```");
            if (end > start) {
                return text.substring(start, end).trim();
            }
        }
        if (text.contains("```")) {
            int start = text.indexOf("```") + 3;
            int end = text.lastIndexOf("```");
            if (end > start) {
                return text.substring(start, end).trim();
            }
        }
        // 查找 JSON 数组
        int arrStart = text.indexOf('[');
        int arrEnd = text.lastIndexOf(']');
        if (arrStart >= 0 && arrEnd > arrStart) {
            return text.substring(arrStart, arrEnd + 1);
        }
        return "[]";
    }
}
