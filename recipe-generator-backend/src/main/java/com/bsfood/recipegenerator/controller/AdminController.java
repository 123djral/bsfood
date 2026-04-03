package com.bsfood.recipegenerator.controller;

import com.bsfood.recipegenerator.entity.FoodMaterial;
import com.bsfood.recipegenerator.entity.Recipe;
import com.bsfood.recipegenerator.entity.User;
import com.bsfood.recipegenerator.service.AdminService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 管理员控制器
 */
@RestController
@RequestMapping("/api/admin")
public class AdminController {

    @Autowired
    private AdminService adminService;

    @GetMapping("/user/list")
    public Map<String, Object> getUserList() {
        Map<String, Object> result = new HashMap<>();
        List<User> userList = adminService.getUserList();
        result.put("code", 200);
        result.put("message", "获取成功");
        result.put("data", userList);
        return result;
    }

    @DeleteMapping("/user/delete")
    public Map<String, Object> deleteUser(@RequestParam Long id) {
        Map<String, Object> result = new HashMap<>();
        boolean success = adminService.deleteUser(id);
        result.put("code", success ? 200 : 400);
        result.put("message", success ? "删除成功" : "删除失败");
        return result;
    }

    @GetMapping("/food/list")
    public Map<String, Object> getFoodList() {
        Map<String, Object> result = new HashMap<>();
        List<FoodMaterial> foodList = adminService.getAllFoodList();
        result.put("code", 200);
        result.put("message", "获取成功");
        result.put("data", foodList);
        return result;
    }

    @DeleteMapping("/food/delete")
    public Map<String, Object> deleteFood(@RequestParam Long id) {
        Map<String, Object> result = new HashMap<>();
        boolean success = adminService.deleteFood(id);
        result.put("code", success ? 200 : 400);
        result.put("message", success ? "删除成功" : "删除失败");
        return result;
    }

    @GetMapping("/recipe/list")
    public Map<String, Object> getRecipeList() {
        Map<String, Object> result = new HashMap<>();
        List<Recipe> recipeList = adminService.getAllRecipeList();
        result.put("code", 200);
        result.put("message", "获取成功");
        result.put("data", recipeList);
        return result;
    }

    @DeleteMapping("/recipe/delete")
    public Map<String, Object> deleteRecipe(@RequestParam Long id) {
        Map<String, Object> result = new HashMap<>();
        boolean success = adminService.deleteRecipe(id);
        result.put("code", success ? 200 : 400);
        result.put("message", success ? "删除成功" : "删除失败");
        return result;
    }
}
