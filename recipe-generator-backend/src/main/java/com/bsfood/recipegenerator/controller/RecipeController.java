package com.bsfood.recipegenerator.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.bsfood.recipegenerator.entity.Recipe;
import com.bsfood.recipegenerator.service.RecipeService;
import com.bsfood.recipegenerator.utils.AiApiClient;
import com.bsfood.recipegenerator.utils.ImageDownloader;
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

    @Autowired
    private AiApiClient aiApiClient;

    @Autowired
    private ImageDownloader imageDownloader;
    
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
     * @param keyword 搜索关键词
     * @param page 页码
     * @param size 每页大小
     * @return 食谱列表
     */
    @GetMapping("/list")
    public Map<String, Object> getList(@RequestParam Long userId,
                                       @RequestParam(required = false) String keyword,
                                       @RequestParam(required = false) Integer page,
                                       @RequestParam(required = false) Integer size) {
        Map<String, Object> result = new HashMap<>();
        int pageNum = (page != null && page > 0) ? page : 1;
        int pageSize = (size != null && size > 0) ? size : 10;

        IPage<Recipe> pageResult;
        if (keyword != null && !keyword.isEmpty()) {
            pageResult = recipeService.searchRecipes(userId, keyword, pageNum, pageSize);
        } else {
            pageResult = recipeService.searchRecipes(userId, null, pageNum, pageSize);
        }
        result.put("code", 200);
        result.put("message", "获取成功");
        result.put("data", pageResult.getRecords());
        result.put("total", pageResult.getTotal());
        result.put("page", pageResult.getCurrent());
        result.put("size", pageResult.getSize());
        result.put("pages", pageResult.getPages());
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

    /**
     * 个人收藏/取消收藏食谱
     * @param recipeId 食谱ID
     * @param userId 用户ID
     * @return 操作结果
     */
    @PostMapping("/collectPersonal")
    public Map<String, Object> collectPersonal(@RequestParam Long recipeId, @RequestParam Long userId) {
        Map<String, Object> result = new HashMap<>();
        boolean success = recipeService.collectRecipe(recipeId, userId);
        result.put("code", 200);
        result.put("message", success ? "操作成功" : "操作失败");
        return result;
    }

    /**
     * 获取用户收藏的食谱列表
     * @param userId 用户ID
     * @param keyword 搜索关键词
     * @param page 页码
     * @param size 每页大小
     * @return 收藏的食谱列表
     */
    @GetMapping("/collected")
    public Map<String, Object> getCollectedRecipes(@RequestParam Long userId,
                                                   @RequestParam(required = false) String keyword,
                                                   @RequestParam(required = false) Integer page,
                                                   @RequestParam(required = false) Integer size) {
        Map<String, Object> result = new HashMap<>();
        int pageNum = (page != null && page > 0) ? page : 1;
        int pageSize = (size != null && size > 0) ? size : 10;

        IPage<Recipe> pageResult;
        if (keyword != null && !keyword.isEmpty()) {
            pageResult = recipeService.searchCollectedRecipes(userId, keyword, pageNum, pageSize);
        } else {
            pageResult = recipeService.searchCollectedRecipes(userId, null, pageNum, pageSize);
        }
        result.put("code", 200);
        result.put("message", "获取成功");
        result.put("data", pageResult.getRecords());
        result.put("total", pageResult.getTotal());
        result.put("page", pageResult.getCurrent());
        result.put("size", pageResult.getSize());
        result.put("pages", pageResult.getPages());
        return result;
    }

    /**
     * 为食谱生成AI图片（本地保存）
     * @param id 食谱ID
     * @return 生成的图片URL
     */
    @PostMapping("/generateImage")
    public Map<String, Object> generateImage(@RequestParam Long id) {
        Map<String, Object> result = new HashMap<>();
        try {
            Recipe recipe = recipeService.getRecipeById(id);
            if (recipe == null) {
                result.put("code", 404);
                result.put("message", "食谱不存在");
                return result;
            }

            // 1. 先检查本地是否已有相同名称的图片
            String localPath = imageDownloader.getLocalImagePath(recipe.getName());
            if (localPath != null) {
                // 本地已有，直接使用
                String imageUrl = "/picture/" + recipe.getName().replaceAll("[\\\\/:*?\"<>|]", "_").replaceAll("\\s+", "_") + ".jpg";
                recipe.setImageUrl(imageUrl);
                recipeService.updateRecipe(recipe);
                result.put("code", 200);
                result.put("message", "使用已有图片");
                result.put("data", Map.of("imageUrl", imageUrl, "localPath", localPath));
                return result;
            }

            // 2. 本地没有，调用MiniMax image-01生成图片
            System.out.println(">>> 开始为食谱[" + recipe.getName() + "]生成AI图片...");
            String aiImageUrl = aiApiClient.generateRecipeImage(
                recipe.getName(),
                recipe.getEnglishName(),
                recipe.getImageKeyword()
            );

            // 3. 下载并保存到本地
            String savedPath = imageDownloader.downloadAndSaveImage(aiImageUrl, recipe.getName());
            if (savedPath != null) {
                // 4. 返回相对路径供前端访问
                String imageUrl = "/picture/" + recipe.getName().replaceAll("[\\\\/:*?\"<>|]", "_").replaceAll("\\s+", "_") + ".jpg";
                recipe.setImageUrl(imageUrl);
                recipeService.updateRecipe(recipe);
                result.put("code", 200);
                result.put("message", "图片生成并保存成功");
                result.put("data", Map.of("imageUrl", imageUrl, "localPath", savedPath));
            } else {
                // 下载失败，返回AI原始URL
                recipe.setImageUrl(aiImageUrl);
                recipeService.updateRecipe(recipe);
                result.put("code", 200);
                result.put("message", "图片生成成功（未保存本地）");
                result.put("data", Map.of("imageUrl", aiImageUrl));
            }
        } catch (Exception e) {
            result.put("code", 500);
            result.put("message", "图片生成失败: " + e.getMessage());
            e.printStackTrace();
        }
        return result;
    }
}