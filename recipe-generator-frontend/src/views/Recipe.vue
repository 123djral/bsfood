<template>
  <div class="recipe">
    <h2>食谱生成</h2>
    <el-form :model="recipeForm" label-width="100px">
      <el-form-item label="食材列表">
        <el-select v-model="recipeForm.foodIds" multiple placeholder="请选择食材">
          <el-option v-for="food in foodList" :key="food.id" :label="food.name" :value="food.id"></el-option>
        </el-select>
      </el-form-item>
      <el-form-item label="烹饪时间">
        <el-input v-model="recipeForm.cookingTime" placeholder="请输入烹饪时间（分钟）"></el-input>
      </el-form-item>
      <el-form-item label="难度等级">
        <el-select v-model="recipeForm.difficultyLevel" placeholder="请选择难度等级">
          <el-option label="简单" value="简单"></el-option>
          <el-option label="中等" value="中等"></el-option>
          <el-option label="困难" value="困难"></el-option>
        </el-select>
      </el-form-item>
      <el-form-item>
        <el-button type="primary" @click="generateRecipe">生成食谱</el-button>
      </el-form-item>
    </el-form>
    
    <div v-if="recipes.length > 0" class="recipe-list">
      <h3>生成的食谱</h3>
      <el-row :gutter="20">
        <el-col :span="8" v-for="recipe in recipes" :key="recipe.id">
          <el-card shadow="hover">
            <template #header>
              <div class="card-header">
                <span>{{ recipe.name }}</span>
              </div>
            </template>
            <div class="card-content">
              <p>烹饪时间：{{ recipe.cookingTime }}分钟</p>
              <p>难度等级：{{ recipe.difficultyLevel }}</p>
              <p>收藏次数：{{ recipe.collectCount }}</p>
              <el-button type="primary" @click="collectRecipe(recipe.id)">收藏</el-button>
            </div>
          </el-card>
        </el-col>
      </el-row>
    </div>
  </div>
</template>

<script>
export default {
  name: 'Recipe',
  data() {
    return {
      recipeForm: {
        foodIds: [],
        cookingTime: '',
        difficultyLevel: ''
      },
      foodList: [
        { id: 1, name: '鸡蛋' },
        { id: 2, name: '西红柿' },
        { id: 3, name: '黄瓜' },
        { id: 4, name: '鸡肉' },
        { id: 5, name: '牛肉' }
      ],
      recipes: []
    }
  },
  methods: {
    generateRecipe() {
      // 调用后端 API 生成食谱
      // 这里使用模拟数据
      this.recipes = [
        {
          id: 1,
          name: '西红柿炒鸡蛋',
          cookingTime: 15,
          difficultyLevel: '简单',
          collectCount: 120
        },
        {
          id: 2,
          name: '黄瓜炒鸡肉',
          cookingTime: 20,
          difficultyLevel: '中等',
          collectCount: 80
        },
        {
          id: 3,
          name: '牛肉炖西红柿',
          cookingTime: 30,
          difficultyLevel: '困难',
          collectCount: 150
        }
      ]
    },
    collectRecipe(recipeId) {
      // 调用后端 API 收藏食谱
      this.$message.success('收藏成功')
    }
  }
}
</script>

<style scoped>
.recipe {
  padding: 20px;
}

.recipe h2 {
  margin-bottom: 20px;
  color: #333;
}

.recipe-list {
  margin-top: 30px;
}

.recipe-list h3 {
  margin-bottom: 20px;
  color: #333;
}

.card-header {
  display: flex;
  justify-content: center;
  align-items: center;
}

.card-content {
  padding: 20px;
}

.card-content p {
  margin-bottom: 10px;
}

.card-content .el-button {
  margin-top: 10px;
  width: 100%;
}
</style>