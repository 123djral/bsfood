package com.bsfood.recipegenerator.controller;

import com.bsfood.recipegenerator.entity.Recipe;
import com.bsfood.recipegenerator.service.RecipeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 食谱控制器
 */
@RestController
@RequestMapping("/api/recipe")
public class RecipeController {
    
    @Autowired
    private RecipeService recipeService;
    
    /**
     * 生成食谱
     * @param userId 用户ID
     * @param foodIds 食材ID列表
     * @param expectCount 期望食谱数量
     * @return 生成结果
     */
    @PostMapping("/generate")
    public Map<String, Object> generate(@RequestParam Long userId, 
                                       @RequestParam List<Long> foodIds, 
                                       @RequestParam int expectCount) {
        Map<String, Object> result = new HashMap<>();
        List<Recipe> recipeList = recipeService.generateRecipe(foodIds, userId, expectCount);
        result.put("code", 200);
        result.put("message", "生成成功");
        result.put("data", Map.of("recipeList", recipeList));
        return result;
    }
    
    /**
     * 保存食谱
     * @param recipe 食谱信息
     * @return 保存结果
     */
    @PostMapping("/save")
    public Map<String, Object> save(@RequestBody Recipe recipe) {
        Map<String, Object> result = new HashMap<>();
        boolean success = recipeService.saveRecipe(recipe);
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
     * 获取食谱列表
     * @param userId 用户ID
     * @return 食谱列表
     */
    @GetMapping("/list")
    public Map<String, Object> getList(@RequestParam Long userId) {
        Map<String, Object> result = new HashMap<>();
        List<Recipe> recipeList = recipeService.getRecipeList(userId);
        result.put("code", 200);
        result.put("message", "获取成功");
        result.put("data", recipeList);
        return result;
    }
    
    /**
     * 获取食谱详情
     * @param id 食谱ID
     * @return 食谱详情
     */
    @GetMapping("/detail")
    public Map<String, Object> getDetail(@RequestParam Long id) {
        Map<String, Object> result = new HashMap<>();
        Recipe recipe = recipeService.getRecipeById(id);
        result.put("code", 200);
        result.put("message", "获取成功");
        result.put("data", recipe);
        return result;
    }
    
    /**
     * 更新食谱
     * @param recipe 食谱信息
     * @return 更新结果
     */
    @PutMapping("/update")
    public Map<String, Object> update(@RequestBody Recipe recipe) {
        Map<String, Object> result = new HashMap<>();
        boolean success = recipeService.updateRecipe(recipe);
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
     * 删除食谱
     * @param id 食谱ID
     * @return 删除结果
     */
    @DeleteMapping("/delete")
    public Map<String, Object> delete(@RequestParam Long id) {
        Map<String, Object> result = new HashMap<>();
        boolean success = recipeService.deleteRecipe(id);
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
     * 收藏食谱
     * @param id 食谱ID
     * @return 收藏结果
     */
    @PostMapping("/collect")
    public Map<String, Object> collect(@RequestParam Long id) {
        Map<String, Object> result = new HashMap<>();
        boolean success = recipeService.collectRecipe(id);
        if (success) {
            result.put("code", 200);
            result.put("message", "收藏成功");
        } else {
            result.put("code", 400);
            result.put("message", "收藏失败");
        }
        return result;
    }
}