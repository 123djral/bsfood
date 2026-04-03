package com.bsfood.recipegenerator.controller;

import com.bsfood.recipegenerator.entity.Nutrition;
import com.bsfood.recipegenerator.service.NutritionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

/**
 * 营养控制器
 */
@RestController
@RequestMapping("/api/nutrition")
public class NutritionController {
    
    @Autowired
    private NutritionService nutritionService;
    
    /**
     * 分析食谱营养成分
     * @param recipeId 食谱ID
     * @return 营养分析结果
     */
    @GetMapping("/analyze")
    public Map<String, Object> analyze(@RequestParam Long recipeId) {
        Map<String, Object> result = new HashMap<>();
        Map<String, Object> nutritionData = nutritionService.analyzeNutrition(recipeId);
        if (nutritionData != null) {
            result.put("code", 200);
            result.put("message", "分析成功");
            result.put("data", nutritionData);
        } else {
            result.put("code", 400);
            result.put("message", "分析失败");
        }
        return result;
    }
    
    /**
     * 保存营养数据
     * @param nutrition 营养数据
     * @return 保存结果
     */
    @PostMapping("/save")
    public Map<String, Object> save(@RequestBody Nutrition nutrition) {
        Map<String, Object> result = new HashMap<>();
        boolean success = nutritionService.saveNutrition(nutrition);
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
     * 获取营养数据
     * @param id 营养数据ID
     * @return 营养数据
     */
    @GetMapping("/detail")
    public Map<String, Object> getDetail(@RequestParam Long id) {
        Map<String, Object> result = new HashMap<>();
        Nutrition nutrition = nutritionService.getNutritionById(id);
        result.put("code", 200);
        result.put("message", "获取成功");
        result.put("data", nutrition);
        return result;
    }
    
    /**
     * 更新营养数据
     * @param nutrition 营养数据
     * @return 更新结果
     */
    @PutMapping("/update")
    public Map<String, Object> update(@RequestBody Nutrition nutrition) {
        Map<String, Object> result = new HashMap<>();
        boolean success = nutritionService.updateNutrition(nutrition);
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
     * 删除营养数据
     * @param id 营养数据ID
     * @return 删除结果
     */
    @DeleteMapping("/delete")
    public Map<String, Object> delete(@RequestParam Long id) {
        Map<String, Object> result = new HashMap<>();
        boolean success = nutritionService.deleteNutrition(id);
        if (success) {
            result.put("code", 200);
            result.put("message", "删除成功");
        } else {
            result.put("code", 400);
            result.put("message", "删除失败");
        }
        return result;
    }
}