package com.bsfood.recipegenerator.service;

import com.bsfood.recipegenerator.entity.FoodMaterial;
import com.bsfood.recipegenerator.entity.Recipe;
import com.bsfood.recipegenerator.entity.User;

import java.util.List;

/**
 * 管理员服务接口
 */
public interface AdminService {

    List<User> getUserList();

    boolean deleteUser(Long id);

    List<FoodMaterial> getAllFoodList();

    boolean deleteFood(Long id);

    List<Recipe> getAllRecipeList();

    boolean deleteRecipe(Long id);
}
