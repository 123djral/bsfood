<template>
  <div class="collection page-shell">
    <div class="page-header">
      <div>
        <h2 class="page-title">我的收藏</h2>
        <p class="page-subtitle">查看您收藏的所有食谱，随时进行营养分析。</p>
      </div>
    </div>

    <el-card shadow="hover" class="table-card">
      <div class="list-header">
        <el-input v-model="searchKeyword" placeholder="搜索食谱名称" style="width: 200px;" clearable @clear="loadCollectedRecipes" @keyup.enter="loadCollectedRecipes">
          <template #prefix><el-icon><Search /></el-icon></template>
        </el-input>
        <el-button type="primary" size="small" @click="loadCollectedRecipes">搜索</el-button>
      </div>
      <el-table :data="collectedRecipes" style="width: 100%" v-loading="loadingCollected">
        <el-table-column prop="name" label="食谱名称"></el-table-column>
        <el-table-column prop="cookingTime" label="烹饪时间(分钟)" width="130"></el-table-column>
        <el-table-column prop="difficultyLevel" label="难度" width="80"></el-table-column>
        <el-table-column label="操作" width="200">
          <template #default="scope">
            <el-button size="small" @click="viewDetail(scope.row)">详情</el-button>
            <el-button size="small" type="warning" @click="uncollectRecipe(scope.row.id)">取消收藏</el-button>
          </template>
        </el-table-column>
      </el-table>
      <el-pagination
        v-if="totalCount > 0"
        layout="prev, pager, next"
        :total="totalCount"
        :page-size="pageSize"
        v-model:current-page="currentPage"
        @current-change="handlePageChange"
        style="margin-top: 16px; text-align: right;"
      />
    </el-card>

    <el-empty v-if="collectedRecipes.length === 0 && !loadingCollected" description="暂无收藏食谱"></el-empty>

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
          </div>
        </div>
        <el-divider></el-divider>
        <h4>🥗 所需食材</h4>
        <div class="ingredients-list">
          <el-tag v-for="ingredient in recipeIngredients" :key="ingredient.id" type="success" size="large" class="ingredient-tag">
            {{ ingredient.name }} ({{ ingredient.quantity }}g)
          </el-tag>
        </div>
        <div class="detail-section">
          <h4>🔧 所需工具</h4>
          <div class="tools-list">
            <el-tag v-for="tool in recipeTools" :key="tool" size="large" class="tool-tag">
              {{ tool }}
            </el-tag>
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
          <el-button type="success" @click="analyzeRecipeNutrition(currentRecipe.id)">营养分析</el-button>
        </div>
      </div>
    </el-dialog>
  </div>
</template>

<script>
import { Search } from '@element-plus/icons-vue'
import { recipeApi, foodApi } from '../api/index.js'

export default {
  name: 'Collection',
  components: { Search },
  data() {
    return {
      collectedRecipes: [],
      loadingCollected: false,
      searchKeyword: '',
      currentPage: 1,
      pageSize: 10,
      totalCount: 0,
      detailVisible: false,
      currentRecipe: null,
      recipeImage: '',
      imageLoading: false,
      foodList: [],
      recipeIngredients: [],
      recipeTools: []
    }
  },
  mounted() {
    this.loadCollectedRecipes()
    this.loadFoodList()
  },
  methods: {
    async loadFoodList() {
      try {
        const userId = this.getCurrentUserId()
        const res = await foodApi.listAll(userId)
        if (res.code === 200) {
          this.foodList = res.data || []
        }
      } catch (e) {
        console.error('加载食材列表失败:', e)
      }
    },
    async loadCollectedRecipes() {
      const userId = this.getCurrentUserId()
      if (!userId) return
      this.loadingCollected = true
      try {
        const res = await recipeApi.collected(userId, this.searchKeyword || null, this.currentPage, this.pageSize)
        if (res.code === 200) {
          this.collectedRecipes = res.data || []
          this.totalCount = res.total || 0
        }
      } catch (e) {
        console.error('加载收藏列表失败:', e)
      } finally {
        this.loadingCollected = false
      }
    },
    handlePageChange(page) {
      this.currentPage = page
      this.loadCollectedRecipes()
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
    viewDetail(recipe) {
      this.currentRecipe = recipe
      this.detailVisible = true
      this.recipeImage = ''
      this.imageLoading = false
      this.recipeIngredients = this.parseRecipeIngredients(recipe.foodIds)
      this.recipeTools = this.extractToolsFromSteps(recipe.steps)
      // 优先使用已生成的AI图片
      if (recipe.imageUrl) {
        this.recipeImage = recipe.imageUrl
        this.imageLoading = false
      }
    },
    parseRecipeIngredients(foodIdsStr) {
      if (!foodIdsStr) return []
      try {
        const foodIds = JSON.parse(foodIdsStr)
        if (!Array.isArray(foodIds)) return []
        return this.foodList.filter(f => foodIds.includes(f.id))
      } catch {
        return []
      }
    },
    extractToolsFromSteps(stepsText) {
      if (!stepsText) return []
      const toolsSet = new Set()
      const toolsKeywords = [
        '锅', '炒锅', '蒸锅', '电饭锅', '高压锅', '砂锅', '烤箱', '微波炉',
        '刀', '砧板', '菜板', '碗', '盘', '碟', '勺', '铲', '漏勺',
        '蒸笼', '蒸架', '筛子', '漏斗', '保鲜膜', '烤盘', '模具',
        '榨汁机', '搅拌机', '料理机', '筷子', '叉子', '勺子'
      ]
      for (const tool of toolsKeywords) {
        if (stepsText.includes(tool)) {
          toolsSet.add(tool)
        }
      }
      return Array.from(toolsSet)
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

.list-header {
  display: flex;
  gap: 10px;
  margin-bottom: 10px;
}

.table-card :deep(.el-card__body) {
  padding: 20px;
}

.card-header {
  display: flex;
  justify-content: space-between;
  font-weight: 700;
  font-size: 18px;
  color: var(--text-primary);
}

.dialog-content {
  padding: 0 10px;
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

.detail-steps .step-item {
  padding: 14px 16px;
  background: #fff;
  border: 1px solid #e8edf4;
  border-radius: 12px;
  margin-bottom: 10px;
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

.detail-section {
  margin-bottom: 16px;
}

.detail-section h4 {
  margin: 0 0 12px;
  font-size: 16px;
  color: var(--text-primary);
}

.ingredients-list,
.tools-list {
  display: flex;
  flex-wrap: wrap;
  gap: 10px;
}

.ingredient-tag,
.tool-tag {
  padding: 8px 14px;
  font-size: 14px;
}

.dialog-actions {
  margin-top: 24px;
  display: flex;
  gap: 12px;
  justify-content: center;
}

@media (max-width: 768px) {
  .recipe-header {
    flex-direction: column;
  }

  .recipe-image,
  .image-placeholder {
    width: 100%;
    height: auto;
    aspect-ratio: 4/3;
  }
}
</style>
