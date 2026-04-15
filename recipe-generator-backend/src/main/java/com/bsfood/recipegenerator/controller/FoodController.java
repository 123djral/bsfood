package com.bsfood.recipegenerator.controller;

import com.bsfood.recipegenerator.entity.FoodMaterial;
import com.bsfood.recipegenerator.entity.Nutrition;
import com.bsfood.recipegenerator.service.FoodService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

/**
 * 食材控制器
 */
@RestController
@RequestMapping("/api/food")
public class FoodController {
    
    @Autowired
    private FoodService foodService;
    
    /**
     * 识别食材
     * @param text 文本食材信息
     * @param image 图像食材信息
     * @param type 输入类型
     * @param userId 用户ID
     * @return 识别结果
     */
    @PostMapping("/recognize")
    public Map<String, Object> recognize(@RequestBody(required = false) Map<String, Object> payload,
                                        @RequestParam(required = false) String text,
                                        @RequestParam(required = false) String image,
                                        @RequestParam(required = false) String type,
                                        @RequestParam(required = false) Long userId) {
        Map<String, Object> request = payload == null ? Map.of() : payload;
        String resolvedText = text != null ? text : Objects.toString(request.get("text"), null);
        String resolvedImage = image != null ? image : Objects.toString(request.get("image"), null);
        String resolvedType = type != null ? type : Objects.toString(request.get("type"), null);
        Long resolvedUserId = userId != null ? userId : toLong(request.get("userId"));

        Map<String, Object> result = new HashMap<>();
        List<FoodMaterial> foodList = foodService.recognizeFood(resolvedText, resolvedImage, resolvedType, resolvedUserId);
        result.put("code", 200);
        result.put("message", "识别成功");
        result.put("data", Map.of("foodList", foodList, "status", "success"));
        return result;
    }

    /**
     * 保存食材
     * @param foodMaterial 食材信息
     * @param userId 用户ID
     * @return 保存结果
     */
    @PostMapping("/save")
    public Map<String, Object> save(@RequestBody FoodMaterial foodMaterial,
                                    @RequestParam(required = false) Long userId) {
        Map<String, Object> result = new HashMap<>();
        // 从请求体中获取 userId（如果前端传了的话）
        Long resolvedUserId = userId != null ? userId : foodMaterial.getUserId();
        boolean success = foodService.saveFood(foodMaterial, resolvedUserId);
        if (success) {
            result.put("code", 200);
            result.put("message", "保存成功");
        } else {
            result.put("code", 400);
            result.put("message", "保存失败");
        }
        return result;
    }

    /**
     * 获取食材列表
     * @param userId 用户ID
     * @return 食材列表
     */
    @GetMapping("/list")
    public Map<String, Object> getList(@RequestParam(required = false) Long userId) {
        Map<String, Object> result = new HashMap<>();
        List<FoodMaterial> foodList = foodService.getFoodList(userId);
        result.put("code", 200);
        result.put("message", "获取成功");
        result.put("data", foodList);
        return result;
    }

    /**
     * 获取食材详情
     * @param id 食材ID
     * @param userId 用户ID
     * @return 食材详情
     */
    @GetMapping("/detail")
    public Map<String, Object> getDetail(@RequestParam Long id,
                                          @RequestParam(required = false) Long userId) {
        Map<String, Object> result = new HashMap<>();
        FoodMaterial foodMaterial = foodService.getFoodById(id, userId);
        result.put("code", 200);
        result.put("message", "获取成功");
        result.put("data", foodMaterial);
        return result;
    }

    /**
     * 更新食材
     * @param foodMaterial 食材信息
     * @param userId 用户ID
     * @return 更新结果
     */
    @PutMapping("/update")
    public Map<String, Object> update(@RequestBody FoodMaterial foodMaterial,
                                      @RequestParam(required = false) Long userId) {
        Map<String, Object> result = new HashMap<>();
        Long resolvedUserId = userId != null ? userId : foodMaterial.getUserId();
        boolean success = foodService.updateFood(foodMaterial, resolvedUserId);
        if (success) {
            result.put("code", 200);
            result.put("message", "更新成功");
        } else {
            result.put("code", 400);
            result.put("message", "更新失败");
        }
        return result;
    }

    /**
     * 删除食材
     * @param id 食材ID
     * @param userId 用户ID
     * @return 删除结果
     */
    @DeleteMapping("/delete")
    public Map<String, Object> delete(@RequestParam Long id,
                                      @RequestParam(required = false) Long userId) {
        Map<String, Object> result = new HashMap<>();
        boolean success = foodService.deleteFood(id, userId);
        if (success) {
            result.put("code", 200);
            result.put("message", "删除成功");
        } else {
            result.put("code", 400);
            result.put("message", "删除失败");
        }
        return result;
    }

    private Long toLong(Object value) {
        if (value == null) return null;
        if (value instanceof Long) return (Long) value;
        if (value instanceof Integer) return ((Integer) value).longValue();
        if (value instanceof String) {
            try {
                return Long.parseLong((String) value);
            } catch (NumberFormatException e) {
                return null;
            }
        }
        return null;
    }
    
    /**
     * 获取食材营养数据
     * @param foodId 食材ID
     * @return 营养数据
     */
    @GetMapping("/nutrition")
    public Map<String, Object> getNutrition(@RequestParam Long foodId) {
        Map<String, Object> result = new HashMap<>();
        Nutrition nutrition = foodService.getNutritionByFoodId(foodId);
        result.put("code", 200);
        result.put("message", "获取成功");
        result.put("data", nutrition);
        return result;
    }
    
    /**
     * 推荐食材替代方案
     * @param foodId 食材ID
     * @return 替代食材列表
     */
    @GetMapping("/substitute")
    public Map<String, Object> getSubstitute(@RequestParam Long foodId) {
        Map<String, Object> result = new HashMap<>();
        List<FoodMaterial> substituteList = foodService.getSubstituteFood(foodId);
        result.put("code", 200);
        result.put("message", "获取成功");
        result.put("data", substituteList);
        return result;
    }

    /**
     * 根据食材名称推荐替代方案
     * @param foodName 食材名称
     * @param foodType 食材类型
     * @return 替代食材列表
     */
    @GetMapping("/substituteByName")
    public Map<String, Object> getSubstituteByName(@RequestParam String foodName, @RequestParam String foodType) {
        Map<String, Object> result = new HashMap<>();
        List<FoodMaterial> substituteList = foodService.getSubstituteByName(foodName, foodType);
        result.put("code", 200);
        result.put("message", "获取成功");
        result.put("data", substituteList);
        return result;
    }
}