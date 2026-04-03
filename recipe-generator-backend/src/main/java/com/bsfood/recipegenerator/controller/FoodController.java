package com.bsfood.recipegenerator.controller;

import com.bsfood.recipegenerator.entity.FoodMaterial;
import com.bsfood.recipegenerator.entity.Nutrition;
import com.bsfood.recipegenerator.service.FoodService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
     * @return 识别结果
     */
    @PostMapping("/recognize")
    public Map<String, Object> recognize(@RequestParam(required = false) String text, 
                                        @RequestParam(required = false) String image, 
                                        @RequestParam String type) {
        Map<String, Object> result = new HashMap<>();
        List<FoodMaterial> foodList = foodService.recognizeFood(text, image, type);
        result.put("code", 200);
        result.put("message", "识别成功");
        result.put("data", Map.of("foodList", foodList, "status", "success"));
        return result;
    }
    
    /**
     * 保存食材
     * @param foodMaterial 食材信息
     * @return 保存结果
     */
    @PostMapping("/save")
    public Map<String, Object> save(@RequestBody FoodMaterial foodMaterial) {
        Map<String, Object> result = new HashMap<>();
        boolean success = foodService.saveFood(foodMaterial);
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
     * @return 食材列表
     */
    @GetMapping("/list")
    public Map<String, Object> getList() {
        Map<String, Object> result = new HashMap<>();
        List<FoodMaterial> foodList = foodService.getFoodList();
        result.put("code", 200);
        result.put("message", "获取成功");
        result.put("data", foodList);
        return result;
    }
    
    /**
     * 获取食材详情
     * @param id 食材ID
     * @return 食材详情
     */
    @GetMapping("/detail")
    public Map<String, Object> getDetail(@RequestParam Long id) {
        Map<String, Object> result = new HashMap<>();
        FoodMaterial foodMaterial = foodService.getFoodById(id);
        result.put("code", 200);
        result.put("message", "获取成功");
        result.put("data", foodMaterial);
        return result;
    }
    
    /**
     * 更新食材
     * @param foodMaterial 食材信息
     * @return 更新结果
     */
    @PutMapping("/update")
    public Map<String, Object> update(@RequestBody FoodMaterial foodMaterial) {
        Map<String, Object> result = new HashMap<>();
        boolean success = foodService.updateFood(foodMaterial);
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
     * @return 删除结果
     */
    @DeleteMapping("/delete")
    public Map<String, Object> delete(@RequestParam Long id) {
        Map<String, Object> result = new HashMap<>();
        boolean success = foodService.deleteFood(id);
        if (success) {
            result.put("code", 200);
            result.put("message", "删除成功");
        } else {
            result.put("code", 400);
            result.put("message", "删除失败");
        }
        return result;
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
}