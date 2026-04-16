<template>
  <div class="recipe page-shell">
    <div class="page-header">
      <div>
        <h2 class="page-title">食谱生成</h2>
        <p class="page-subtitle">基于现有食材快速生成候选食谱，并查看收藏、营养分析与详情。</p>
      </div>
    </div>

    <el-card shadow="hover" class="form-card">
      <el-form :model="recipeForm" label-width="100px">
        <el-form-item label="选择食材">
          <el-select v-model="recipeForm.foodIds" multiple placeholder="请选择食材" style="width: 100%;">
            <el-option v-for="food in foodList" :key="food.id" :label="food.name + ' (' + food.quantity + 'g)'" :value="food.id"></el-option>
          </el-select>
        </el-form-item>
        <el-form-item label="食谱数量">
          <el-input-number v-model="recipeForm.expectCount" :min="1" :max="5"></el-input-number>
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="generateRecipe" :loading="generating">生成食谱</el-button>
        </el-form-item>
      </el-form>
    </el-card>

    <div v-if="recipes.length > 0" class="recipe-list">
      <div class="section-heading">
        <h3 class="section-title">生成的食谱</h3>
      </div>
      <el-row :gutter="20" class="recipe-grid">
        <el-col :xs="24" :md="12" :xl="8" v-for="recipe in recipes" :key="recipe.id">
          <el-card shadow="hover" class="recipe-card">
            <template #header>
              <div class="card-header">
                <span>{{ recipe.name }}</span>
              </div>
            </template>
            <div class="card-content">
              <p><strong>烹饪时间：</strong>{{ recipe.cookingTime }}分钟</p>
              <p><strong>难度等级：</strong>{{ recipe.difficultyLevel }}</p>
              <el-divider></el-divider>
              <div class="steps-section">
                <strong>烹饪步骤：</strong>
                <div class="steps-list">
                  <div v-for="(step, index) in parseSteps(recipe.steps)" :key="index" class="step-item">
                    <span class="step-number">{{ index + 1 }}</span>
                    <span class="step-text">{{ step }}</span>
                  </div>
                </div>
              </div>
              <div class="action-row">
                <el-button type="primary" size="small" @click="collectRecipe(recipe.id)">收藏</el-button>
                <el-button type="success" size="small" @click="analyzeRecipeNutrition(recipe.id)">营养分析</el-button>
              </div>
            </div>
          </el-card>
        </el-col>
      </el-row>
    </div>

    <el-divider v-if="recipes.length > 0"></el-divider>

    <div class="my-recipes">
      <div class="section-heading">
        <h3 class="section-title">我的食谱</h3>
      </div>
      <el-card shadow="hover" class="table-card">
        <el-table :data="myRecipes" style="width: 100%" v-loading="loadingMyRecipes">
          <el-table-column prop="name" label="食谱名称"></el-table-column>
          <el-table-column prop="cookingTime" label="烹饪时间(分钟)" width="130"></el-table-column>
          <el-table-column prop="difficultyLevel" label="难度" width="80"></el-table-column>
          <el-table-column label="操作" width="200">
            <template #default="scope">
              <el-button size="small" @click="viewDetail(scope.row)">详情</el-button>
              <el-button size="small" type="danger" @click="deleteRecipe(scope.row.id)">删除</el-button>
            </template>
          </el-table-column>
        </el-table>
      </el-card>
    </div>

    <el-dialog v-model="detailVisible" title="食谱详情" width="800px">
      <div v-if="currentRecipe" class="dialog-content">
        <div class="recipe-header">
          <div class="recipe-image-section">
            <img v-if="recipeImage" :src="recipeImage" alt="美食效果图" class="recipe-image" />
            <div v-else-if="imageLoading" class="image-placeholder loading">
              <span class="loading-text">加载图片中...</span>
            </div>
            <div v-else class="image-placeholder">
              <span class="placeholder-icon">🍽️</span>
              <span class="placeholder-text">暂无图片</span>
            </div>
          </div>
          <div class="recipe-info">
            <h2 class="recipe-title">{{ currentRecipe.name }}</h2>
            <div class="recipe-meta">
              <el-tag type="warning">⏱ 约 {{ currentRecipe.cookingTime }} 分钟</el-tag>
              <el-tag type="info">📊 {{ currentRecipe.difficultyLevel }}</el-tag>
            </div>
            <div class="recipe-tags">
              <span class="recipe-tag" @click="refreshImage">
                <span>🔄</span> 换一张
              </span>
            </div>
          </div>
        </div>
        <el-divider></el-divider>
        <h4>📝 详细烹饪步骤</h4>
        <div class="steps-list detail-steps">
          <div v-for="(step, index) in parseSteps(currentRecipe.steps)" :key="index" class="step-item">
            <div class="step-number-circle">{{ index + 1 }}</div>
            <div class="step-content">
              <span class="step-text">{{ step }}</span>
            </div>
          </div>
        </div>
        <div class="dialog-actions">
          <el-button type="primary" @click="collectRecipe(currentRecipe.id)">收藏此食谱</el-button>
          <el-button type="success" @click="analyzeRecipeNutrition(currentRecipe.id)">营养分析</el-button>
        </div>
      </div>
    </el-dialog>
  </div>
</template>

<script>
import { foodApi, recipeApi, userApi } from '../api/index.js'

export default {
  name: 'Recipe',
  data() {
    return {
      recipeForm: {
        foodIds: [],
        expectCount: 3
      },
      foodList: [],
      recipes: [],
      myRecipes: [],
      generating: false,
      loadingMyRecipes: false,
      detailVisible: false,
      currentRecipe: null,
      userPreference: null,
      recipeImage: '',
      imageLoading: false
    }
  },
  mounted() {
    this.loadFoodList()
    this.loadMyRecipes()
    this.loadUserPreference()
  },
  methods: {
    async loadFoodList() {
      try {
        const userId = this.getCurrentUserId()
        const res = await foodApi.list(userId)
        if (res.code === 200) {
          this.foodList = res.data || []
        }
      } catch (e) {
        console.error('加载食材列表失败:', e)
      }
    },
    getCurrentUserId() {
      const userId = localStorage.getItem('userId')
      if (userId) {
        return parseInt(userId, 10)
      }
      return null
    },
    parseSteps(stepsText) {
      if (!stepsText) return []
      // 按数字序号分割步骤 (1. 2. 或 1、2、)
      const steps = stepsText.split(/(?:\d+[.、]|\r?\n)/).filter(s => s.trim())
      return steps.map(s => s.trim()).filter(s => s.length > 0)
    },
    async loadMyRecipes() {
      const userId = this.getCurrentUserId()
      if (!userId) return
      this.loadingMyRecipes = true
      try {
        const res = await recipeApi.list(userId)
        if (res.code === 200) {
          this.myRecipes = res.data || []
        }
      } catch (e) {
        console.error('加载食谱列表失败:', e)
      } finally {
        this.loadingMyRecipes = false
      }
    },
    async loadUserPreference() {
      const userId = this.getCurrentUserId()
      if (!userId) return
      try {
        const res = await userApi.getPreference(userId)
        if (res.code === 200) {
          this.userPreference = res.data
        }
      } catch (e) {
        console.error('加载用户偏好失败:', e)
      }
    },
    validateIngredientConflicts(selectedFoodIds) {
      if (!this.userPreference?.dietLimit) return { hasConflict: false, conflicts: [] }
      const dietLimits = this.userPreference.dietLimit.split(/[,，、]/).map(s => s.trim()).filter(s => s)
      const selectedFoods = this.foodList.filter(f => selectedFoodIds.includes(f.id))
      const conflicts = selectedFoods.filter(food =>
        dietLimits.some(limit => food.name.includes(limit) || limit.includes(food.name))
      )
      return { hasConflict: conflicts.length > 0, conflicts: conflicts.map(c => c.name) }
    },
    async generateRecipe() {
      if (this.recipeForm.foodIds.length === 0) {
        this.$message.warning('请选择至少一个食材')
        return
      }
      const userId = this.getCurrentUserId()
      if (!userId) {
        this.$message.warning('请先登录')
        return
      }

      // 检查食材是否与饮食限制冲突
      const { hasConflict, conflicts } = this.validateIngredientConflicts(this.recipeForm.foodIds)
      if (hasConflict) {
        try {
          await this.$confirm(
            `您选择的食材 "${conflicts.join('、')}" 与您的饮食限制（${this.userPreference.dietLimit}）冲突。\n\n是否仍要使用这些食材生成食谱？`,
            '食材冲突提醒',
            { type: 'warning', confirmButtonText: '仍要生成', cancelButtonText: '取消' }
          )
        } catch {
          this.$message.info('已取消生成')
          return
        }
      }

      this.generating = true
      try {
        const res = await recipeApi.generate(
          userId,
          this.recipeForm.foodIds,
          this.recipeForm.expectCount
        )
        if (res.code === 200) {
          this.recipes = res.data.recipeList || []
          this.$message.success('食谱生成成功')
          this.loadMyRecipes()
        } else {
          this.$message.error(res.message)
        }
      } catch (e) {
        if (e.message && e.message.includes('饮食限制冲突')) {
          this.$message.error(e.message)
        } else {
          this.$message.error('生成失败，请重试')
        }
      } finally {
        this.generating = false
      }
    },
    async collectRecipe(id) {
      const userId = this.getCurrentUserId()
      if (!userId) {
        this.$message.warning('请先登录')
        return
      }
      try {
        const res = await recipeApi.collectPersonal(id, userId)
        if (res.code === 200) {
          this.$message.success('收藏成功')
          this.$router.push('/collection')
        }
      } catch (e) {
        this.$message.error('收藏失败')
      }
    },
    analyzeRecipeNutrition(id) {
      this.$router.push({ path: '/nutrition', query: { recipeId: id } })
    },
    viewDetail(recipe) {
      this.currentRecipe = recipe
      this.detailVisible = true
      // 优先使用已生成的AI图片
      if (recipe.imageUrl) {
        this.recipeImage = recipe.imageUrl
        this.imageLoading = false
      } else {
        // 调用后端生成MiniMax图片
        this.generateMiniMaxImage(recipe.id)
      }
    },
    async generateMiniMaxImage(recipeId) {
      this.imageLoading = true
      this.recipeImage = ''
      try {
        const res = await fetch(`/api/recipe/generateImage?id=${recipeId}`, {
          method: 'POST',
          headers: { 'Content-Type': 'application/json' }
        })
        const data = await res.json()
        if (data.code === 200 && data.data && data.data.imageUrl) {
          this.recipeImage = data.data.imageUrl
          // 更新当前食谱的图片URL
          if (this.currentRecipe) {
            this.currentRecipe.imageUrl = data.data.imageUrl
          }
          this.imageLoading = false
          return
        }
      } catch (e) {
        console.error('MiniMax图片生成失败:', e)
      }
      // 兜底: 使用Spoonacular搜索
      this.imageLoading = false
      if (this.currentRecipe) {
        this.fetchRecipeImage(this.currentRecipe.imageKeyword || this.currentRecipe.englishName || this.currentRecipe.name)
      }
    },
    // 提取食材关键词用于后备搜索
    extractFoodKeywords(name) {
      const keywords = []
      const foods = ['chicken', 'beef', 'pork', 'fish', 'egg', 'tofu', 'vegetable', 'shrimp', 'rice', 'noodle', 'tomato', 'potato', 'cabbage', 'carrot', 'mushroom', 'corn', 'milk', 'honey', 'garlic', 'ginger']
      const lowerName = name.toLowerCase()
      foods.forEach(food => {
        if (lowerName.includes(food)) {
          keywords.push(food)
        }
      })
      return keywords
    },
    async fetchRecipeImage(searchKeyword) {
      this.imageLoading = true
      this.recipeImage = ''
      const apiKey = '07778492d7b54f5cad6c99e09a4d1cb1'
      try {
        // 策略1: AI优化关键词搜索 (最精准)
        if (searchKeyword) {
          let res = await fetch(`https://api.spoonacular.com/recipes/complexSearch?query=${encodeURIComponent(searchKeyword)}&number=5&apiKey=${apiKey}`)
          let data = await res.json()
          if (data.results && data.results.length > 0) {
            const randomIndex = Math.floor(Math.random() * Math.min(data.results.length, 5))
            this.recipeImage = data.results[randomIndex].image
            this.imageLoading = false
            return
          }
        }

        // 策略2: 提取食材关键词搜索
        const keywords = this.extractFoodKeywords(searchKeyword || '')
        if (keywords.length > 0) {
          let res = await fetch(`https://api.spoonacular.com/recipes/complexSearch?query=${encodeURIComponent(keywords[0])}&number=10&apiKey=${apiKey}`)
          let data = await res.json()
          if (data.results && data.results.length > 0) {
            const randomMeal = data.results[Math.floor(Math.random() * Math.min(data.results.length, 10))]
            this.recipeImage = randomMeal.image
            this.imageLoading = false
            return
          }
        }

        // 策略3: 使用分类随机获取
        const categories = ['chicken', 'beef', 'pork', 'fish', 'vegetarian', 'pasta', 'dessert', 'breakfast', 'salad', 'soup']
        const randomCat = categories[Math.floor(Math.random() * categories.length)]
        let res = await fetch(`https://api.spoonacular.com/recipes/complexSearch?query=${randomCat}&number=10&apiKey=${apiKey}`)
        let data = await res.json()
        if (data.results && data.results.length > 0) {
          const randomMeal = data.results[Math.floor(Math.random() * Math.min(data.results.length, 10))]
          this.recipeImage = randomMeal.image
        }
      } catch (e) {
        console.error('获取食谱图片失败:', e)
      } finally {
        this.imageLoading = false
      }
    },
    refreshImage() {
      if (this.currentRecipe) {
        this.fetchRecipeImage(this.currentRecipe.imageKeyword || this.currentRecipe.englishName || this.currentRecipe.name)
      }
    },
    async deleteRecipe(id) {
      try {
        await this.$confirm('确定要删除这个食谱吗？', '提示', { type: 'warning' })
        const res = await recipeApi.delete(id)
        if (res.code === 200) {
          this.$message.success('删除成功')
          this.loadMyRecipes()
        }
      } catch {
        // cancelled
      }
    }
  }
}
</script>

<style scoped>
.recipe {
  display: flex;
  flex-direction: column;
  gap: 22px;
}

.page-header,
.section-heading {
  display: flex;
  flex-direction: column;
  gap: 4px;
}

.form-card :deep(.el-card__body),
.table-card :deep(.el-card__body) {
  padding: 24px;
}

.recipe-list,
.my-recipes {
  display: flex;
  flex-direction: column;
  gap: 16px;
}

.recipe-grid {
  row-gap: 20px;
}

.recipe-card {
  margin-bottom: 0;
}

.recipe-card :deep(.el-card__header) {
  padding: 20px 22px 0;
  border-bottom: none;
}

.recipe-card :deep(.el-card__body) {
  padding: 16px 22px 22px;
}

.card-header {
  display: flex;
  justify-content: space-between;
  font-weight: 700;
  font-size: 18px;
  color: var(--text-primary);
}

.card-content p {
  margin: 0 0 10px;
  color: var(--text-secondary);
}

.steps-section {
  margin-top: 10px;
}

.steps-list {
  margin-top: 8px;
  display: flex;
  flex-direction: column;
  gap: 10px;
}

.step-item {
  display: flex;
  align-items: flex-start;
  gap: 12px;
  padding: 10px 14px;
  background: #f8fafc;
  border: 1px solid rgba(148, 163, 184, 0.18);
  border-radius: 10px;
  transition: all 0.2s;
}

.step-item:hover {
  background: #f0f7ff;
  border-color: rgba(37, 99, 235, 0.3);
}

.step-number {
  display: flex;
  align-items: center;
  justify-content: center;
  min-width: 24px;
  height: 24px;
  background: var(--primary);
  color: white;
  border-radius: 50%;
  font-size: 12px;
  font-weight: 600;
}

.step-text {
  line-height: 1.6;
  color: var(--text-secondary);
  flex: 1;
}

.steps-text {
  white-space: pre-wrap;
  line-height: 1.8;
  color: var(--text-secondary);
  margin-top: 8px;
  padding: 14px;
  background: #f8fafc;
  border: 1px solid rgba(148, 163, 184, 0.18);
  border-radius: 12px;
}

.recipe-header {
  display: flex;
  gap: 24px;
  align-items: flex-start;
}

.recipe-image-section {
  flex-shrink: 0;
}

.recipe-image {
  width: 280px;
  height: 200px;
  object-fit: cover;
  border-radius: 16px;
  box-shadow: 0 8px 24px rgba(0,0,0,0.12);
  transition: opacity 0.3s ease;
}

.recipe-image[lazy="loaded"] {
  opacity: 1;
}

.image-placeholder {
  width: 280px;
  height: 200px;
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  gap: 8px;
  background: linear-gradient(135deg, #f8fafc, #eef4ff);
  border-radius: 16px;
  border: 2px dashed rgba(148,163,184,0.3);
}

.image-placeholder.loading {
  background: linear-gradient(135deg, #f0f7ff, #e8f4ff);
}

.placeholder-icon {
  font-size: 48px;
}

.placeholder-text {
  font-size: 14px;
  color: var(--text-muted);
}

.loading-text {
  font-size: 14px;
  color: var(--primary);
}

.recipe-info {
  flex: 1;
}

.recipe-title {
  margin: 0 0 12px;
  font-size: 22px;
  color: var(--text-primary);
}

.recipe-meta {
  display: flex;
  gap: 10px;
  flex-wrap: wrap;
}

.recipe-tags {
  margin-top: 12px;
  display: flex;
  gap: 10px;
}

.recipe-tag {
  display: inline-flex;
  align-items: center;
  gap: 4px;
  padding: 6px 12px;
  font-size: 13px;
  color: var(--primary);
  background: var(--primary-soft);
  border-radius: 8px;
  cursor: pointer;
  transition: all 0.2s;
}

.recipe-tag:hover {
  background: rgba(37, 99, 235, 0.2);
}

.detail-steps .step-item {
  padding: 14px 16px;
  background: #fff;
  border: 1px solid #e8edf4;
  border-radius: 12px;
}

.detail-steps .step-number-circle {
  display: flex;
  align-items: center;
  justify-content: center;
  min-width: 32px;
  height: 32px;
  background: linear-gradient(135deg, var(--primary), #4f8fff);
  color: white;
  border-radius: 50%;
  font-size: 14px;
  font-weight: 700;
  flex-shrink: 0;
}

.detail-steps .step-content {
  flex: 1;
}

.detail-steps .step-text {
  font-size: 15px;
  line-height: 1.7;
  color: var(--text-primary);
}

.dialog-actions {
  margin-top: 24px;
  display: flex;
  gap: 12px;
  justify-content: center;
}

.action-row {
  margin-top: 16px;
  display: flex;
  gap: 10px;
  flex-wrap: wrap;
}

.dialog-content h3,
.dialog-content h4 {
  color: var(--text-primary);
}

@media (max-width: 768px) {
  .form-card :deep(.el-card__body),
  .table-card :deep(.el-card__body),
  .recipe-card :deep(.el-card__body) {
    padding: 18px;
  }
}
</style>
