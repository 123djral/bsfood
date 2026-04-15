<template>
  <div class="collection page-shell">
    <div class="page-header">
      <div>
        <h2 class="page-title">我的收藏</h2>
        <p class="page-subtitle">查看您收藏的所有食谱，随时进行营养分析。</p>
      </div>
    </div>

    <div v-if="collectedRecipes.length > 0" class="recipe-list">
      <el-row :gutter="20" class="recipe-grid">
        <el-col :xs="24" :md="12" :xl="8" v-for="recipe in collectedRecipes" :key="recipe.id">
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
                <el-button type="warning" size="small" @click="uncollectRecipe(recipe.id)">取消收藏</el-button>
                <el-button type="success" size="small" @click="analyzeRecipeNutrition(recipe.id)">营养分析</el-button>
              </div>
            </div>
          </el-card>
        </el-col>
      </el-row>
    </div>

    <el-empty v-else description="暂无收藏食谱"></el-empty>
  </div>
</template>

<script>
import { recipeApi } from '../api/index.js'

export default {
  name: 'Collection',
  data() {
    return {
      collectedRecipes: [],
      loadingCollected: false
    }
  },
  mounted() {
    this.loadCollectedRecipes()
  },
  methods: {
    async loadCollectedRecipes() {
      const userId = this.getCurrentUserId()
      if (!userId) return
      this.loadingCollected = true
      try {
        const res = await recipeApi.collected(userId)
        if (res.code === 200) {
          this.collectedRecipes = res.data || []
        }
      } catch (e) {
        console.error('加载收藏列表失败:', e)
      } finally {
        this.loadingCollected = false
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
      const steps = stepsText.split(/(?:\d+[.、]|\r?\n)/).filter(s => s.trim())
      return steps.map(s => s.trim()).filter(s => s.length > 0)
    },
    async uncollectRecipe(id) {
      const userId = this.getCurrentUserId()
      if (!userId) return
      try {
        await this.$confirm('确定要取消收藏吗？', '提示', { type: 'warning' })
        const res = await recipeApi.collectPersonal(id, userId)
        if (res.code === 200) {
          this.$message.success('已取消收藏')
          this.loadCollectedRecipes()
        }
      } catch {
        // cancelled
      }
    },
    analyzeRecipeNutrition(id) {
      this.$router.push({ path: '/nutrition', query: { recipeId: id } })
    }
  }
}
</script>

<style scoped>
.collection {
  display: flex;
  flex-direction: column;
  gap: 22px;
}

.page-header {
  display: flex;
  flex-direction: column;
  gap: 4px;
}

.recipe-list {
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

.action-row {
  margin-top: 16px;
  display: flex;
  gap: 10px;
  flex-wrap: wrap;
}

@media (max-width: 768px) {
  .recipe-card :deep(.el-card__body) {
    padding: 16px 18px 18px;
  }
}
</style>
