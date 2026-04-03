package com.bsfood.recipegenerator.service.impl;

import com.bsfood.recipegenerator.entity.FoodMaterial;
import com.bsfood.recipegenerator.entity.Recipe;
import com.bsfood.recipegenerator.entity.User;
import com.bsfood.recipegenerator.mapper.FoodMaterialMapper;
import com.bsfood.recipegenerator.mapper.RecipeMapper;
import com.bsfood.recipegenerator.mapper.UserMapper;
import com.bsfood.recipegenerator.service.AdminService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 管理员服务实现类
 */
@Service
public class AdminServiceImpl implements AdminService {

    @Autowired
    private UserMapper userMapper;

    @Autowired
    private FoodMaterialMapper foodMaterialMapper;

    @Autowired
    private RecipeMapper recipeMapper;

    @Override
    public List<User> getUserList() {
        return userMapper.selectList(null);
    }

    @Override
    public boolean deleteUser(Long id) {
        return userMapper.deleteById(id) > 0;
    }

    @Override
    public List<FoodMaterial> getAllFoodList() {
        return foodMaterialMapper.selectList(null);
    }

    @Override
    public boolean deleteFood(Long id) {
        return foodMaterialMapper.deleteById(id) > 0;
    }

    @Override
    public List<Recipe> getAllRecipeList() {
        return recipeMapper.selectList(null);
    }

    @Override
    public boolean deleteRecipe(Long id) {
        return recipeMapper.deleteById(id) > 0;
    }
}
