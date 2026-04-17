<template>
  <div class="nutrition page-shell">
    <div class="page-header">
      <div>
        <h2 class="page-title">营养分析</h2>
        <p class="page-subtitle">从已生成食谱中选择目标，查看详细营养成分、膳食评估与改善建议。</p>
      </div>
    </div>

    <el-card shadow="hover" class="form-card">
      <el-form label-width="100px">
        <el-form-item label="选择食谱">
          <el-select
            v-model="selectedRecipeId"
            filterable
            placeholder="搜索或选择要分析的食谱"
            style="width: 100%;"
            :filter-method="filterRecipes"
            @focus="showAllRecipes"
          >
            <div class="recipe-select-dropdown">
              <el-row :gutter="8">
                <el-col :span="5" v-for="r in filteredRecipeList" :key="r.id">
                  <el-option
                    :label="r.name"
                    :value="r.id"
                    :style="{ width: '100%' }"
                  >
                    {{ r.name }}
                  </el-option>
                </el-col>
              </el-row>
            </div>
          </el-select>
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="analyzeNutrition" :loading="analyzing">分析营养</el-button>
        </el-form-item>
      </el-form>
    </el-card>

    <div v-if="nutritionData" class="nutrition-data">
      <div class="section-heading">
        <h3 class="section-title">营养分析结果</h3>
      </div>

      <!-- 综合评分卡片 -->
      <el-card shadow="hover" class="score-card" v-if="nutritionBalance">
        <div class="score-grid">
          <div class="score-item">
            <el-progress type="circle" :percentage="nutritionBalance.overallScore || 0" :width="100" :stroke-width="10">
              <template #default>
                <div class="score-inner">
                  <span class="score-value">{{ nutritionBalance.overallScore || 0 }}</span>
                  <span class="score-label">综合评分</span>
                </div>
              </template>
            </el-progress>
          </div>
          <div class="score-details">
            <div class="score-row">
              <span class="score-name">宏量营养素均衡</span>
              <el-progress :percentage="nutritionBalance.macroBalance || 0" :stroke-width="12" />
            </div>
            <div class="score-row">
              <span class="score-name">微量营养素丰富度</span>
              <el-progress :percentage="nutritionBalance.microBalance || 0" :stroke-width="12" color="#67c23a" />
            </div>
            <div class="sodium-level">
              钠含量水平：<el-tag :type="nutritionBalance.sodiumLevel === '低' ? 'success' : nutritionBalance.sodiumLevel === '高' ? 'danger' : 'warning'" size="small">{{ nutritionBalance.sodiumLevel || '中' }}</el-tag>
            </div>
          </div>
        </div>
      </el-card>

      <!-- 热量与宏量营养素 -->
      <el-card shadow="hover" class="table-card">
        <template #header>
          <div class="card-header"><span>热量与宏量营养素</span></div>
        </template>
        <el-table :data="macroNutritionData" style="width: 100%">
          <el-table-column prop="name" label="营养成分" width="150"></el-table-column>
          <el-table-column prop="value" label="含量"></el-table-column>
          <el-table-column prop="unit" label="单位" width="80"></el-table-column>
          <el-table-column prop="dailyPct" label="占每日推荐%" width="120">
            <template #default="scope">
              <span v-if="scope.row.dailyPct" :class="'pct-' + getPctLevel(scope.row.dailyPct)">
                {{ scope.row.dailyPct }}%
              </span>
              <span v-else>-</span>
            </template>
          </el-table-column>
        </el-table>
      </el-card>

      <!-- 微量营养素 -->
      <el-card shadow="hover" class="table-card">
        <template #header>
          <div class="card-header"><span>维生素与矿物质</span></div>
        </template>
        <el-table :data="microNutritionData" style="width: 100%">
          <el-table-column prop="name" label="营养成分" width="150"></el-table-column>
          <el-table-column prop="value" label="含量"></el-table-column>
          <el-table-column prop="unit" label="单位" width="80"></el-table-column>
          <el-table-column prop="dailyPct" label="占每日推荐%" width="120">
            <template #default="scope">
              <span v-if="scope.row.dailyPct" :class="'pct-' + getPctLevel(scope.row.dailyPct)">
                {{ scope.row.dailyPct }}%
              </span>
              <span v-else>-</span>
            </template>
          </el-table-column>
        </el-table>
      </el-card>

      <!-- 适宜与禁忌人群 -->
      <el-card shadow="hover" class="crowd-card" v-if="suitableCrowd && suitableCrowd.length">
        <template #header>
          <div class="card-header"><span>适宜与禁忌人群</span></div>
        </template>
        <div class="crowd-section">
          <div class="crowd-suitable" v-if="suitableCrowd && suitableCrowd.length">
            <h5>✅ 适宜人群</h5>
            <el-tag v-for="crowd in suitableCrowd" :key="crowd" type="success" class="crowd-tag">{{ crowd }}</el-tag>
          </div>
          <div class="crowd-unsuitable" v-if="contraindications && contraindications.length">
            <h5>❌ 禁忌人群</h5>
            <el-tag v-for="item in contraindications" :key="item" type="danger" class="crowd-tag">{{ item }}</el-tag>
          </div>
        </div>
      </el-card>

      <!-- 营养评估 -->
      <div v-if="evaluation" class="nutrition-report">
        <h4>📊 营养评估</h4>
        <p>{{ evaluation }}</p>
      </div>

      <!-- 改善建议 -->
      <div v-if="suggestion" class="nutrition-report suggestion">
        <h4>💡 改善建议</h4>
        <p>{{ suggestion }}</p>
      </div>

      <!-- 膳食搭配建议 -->
      <div v-if="mealAdvice" class="nutrition-report meal-advice">
        <h4>🍽️ 膳食搭配建议</h4>
        <p>{{ mealAdvice }}</p>
      </div>
    </div>
  </div>
</template>

<script>
import { recipeApi, nutritionApi } from '../api/index.js'

export default {
  name: 'Nutrition',
  data() {
    return {
      selectedRecipeId: null,
      recipeList: [],
      filteredRecipeList: [],
      analyzing: false,
      nutritionData: null,
      macroNutritionData: [],
      microNutritionData: [],
      nutritionBalance: null,
      dailyValuePcts: null,
      evaluation: '',
      suggestion: '',
      mealAdvice: '',
      suitableCrowd: [],
      contraindications: []
    }
  },
  mounted() {
    this.loadRecipes()
    // 如果从食谱页面跳转过来
    if (this.$route.query.recipeId) {
      this.selectedRecipeId = parseInt(this.$route.query.recipeId)
      this.$nextTick(() => this.analyzeNutrition())
    }
  },
  methods: {
    getCurrentUserId() {
      const userId = localStorage.getItem('userId')
      if (userId) {
        return parseInt(userId, 10)
      }
      return null
    },
    async loadRecipes() {
      const userId = this.getCurrentUserId()
      if (!userId) return
      try {
        const res = await recipeApi.list(userId)
        if (res.code === 200) {
          this.recipeList = res.data || []
          this.filteredRecipeList = this.recipeList
        }
      } catch (e) {
        console.error('加载食谱列表失败:', e)
      }
    },
    filterRecipes(query) {
      if (query) {
        this.filteredRecipeList = this.recipeList.filter(recipe =>
          recipe.name.toLowerCase().includes(query.toLowerCase())
        )
      } else {
        this.filteredRecipeList = this.recipeList
      }
    },
    showAllRecipes() {
      this.filteredRecipeList = this.recipeList
    },
    async analyzeNutrition() {
      if (!this.selectedRecipeId) {
        this.$message.warning('请选择一个食谱')
        return
      }
      this.analyzing = true
      this.nutritionData = null
      try {
        const res = await nutritionApi.analyze(this.selectedRecipeId)
        if (res.code === 200) {
          const data = res.data
          this.nutritionData = data.nutritionData
          this.evaluation = data.evaluation || ''
          this.suggestion = data.suggestion || ''
          this.mealAdvice = data.mealAdvice || ''
          this.suitableCrowd = data.suitableCrowd || []
          this.contraindications = data.contraindications || []
          this.nutritionBalance = data.nutritionBalance || null
          this.dailyValuePcts = data.dailyValuePercentages || null

          const nd = data.nutritionData || {}
          const dvp = data.dailyValuePercentages || {}

          this.macroNutritionData = [
            { name: '总热量', value: nd.calorie || '-', unit: '大卡', dailyPct: dvp.caloriePct },
            { name: '每100g热量', value: nd.caloriePerHundred || '-', unit: '大卡/100g', dailyPct: null },
            { name: '蛋白质', value: nd.protein || '-', unit: 'g', dailyPct: dvp.proteinPct },
            { name: '脂肪', value: nd.fat || '-', unit: 'g', dailyPct: dvp.fatPct },
            { name: '饱和脂肪酸', value: nd.saturatedFat || '-', unit: 'g', dailyPct: null },
            { name: '不饱和脂肪酸', value: nd.unsaturatedFat || '-', unit: 'g', dailyPct: null },
            { name: '碳水化合物', value: nd.carbohydrate || '-', unit: 'g', dailyPct: dvp.carbohydratePct },
            { name: '膳食纤维', value: nd.dietaryFiber || '-', unit: 'g', dailyPct: dvp.fiberPct },
            { name: '钠', value: nd.sodium || '-', unit: 'mg', dailyPct: dvp.sodiumPct },
            { name: '胆固醇', value: nd.cholesterol || '-', unit: 'mg', dailyPct: null }
          ]

          this.microNutritionData = [
            { name: '维生素A', value: nd.vitaminA || '-', unit: 'μg', dailyPct: null },
            { name: '维生素C', value: nd.vitaminC || '-', unit: 'mg', dailyPct: null },
            { name: '维生素E', value: nd.vitaminE || '-', unit: 'mg', dailyPct: null },
            { name: '钙', value: nd.calcium || '-', unit: 'mg', dailyPct: null },
            { name: '铁', value: nd.iron || '-', unit: 'mg', dailyPct: null },
            { name: '锌', value: nd.zinc || '-', unit: 'mg', dailyPct: null },
            { name: '硒', value: nd.selenium || '-', unit: 'μg', dailyPct: null }
          ]

          this.$message.success('分析完成')
        } else {
          this.$message.error(res.message)
        }
      } catch (e) {
        this.$message.error('分析失败，请重试')
      } finally {
        this.analyzing = false
      }
    },
    getPctLevel(pct) {
      if (!pct) return 'normal'
      if (pct < 15) return 'low'
      if (pct > 30) return 'high'
      return 'normal'
    }
  }
}
</script>

<style scoped>
.nutrition {
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

.table-card :deep(.el-card__header),
.score-card :deep(.el-card__header),
.crowd-card :deep(.el-card__header) {
  padding: 20px 24px 0;
  border-bottom: none;
}

.recipe-select-dropdown {
  padding: 8px;
}

.recipe-select-dropdown .el-select-dropdown__item {
  height: 32px;
  line-height: 32px;
}

.nutrition-data {
  display: flex;
  flex-direction: column;
  gap: 16px;
}

.card-header {
  display: flex;
  justify-content: space-between;
  font-size: 18px;
  font-weight: 700;
  color: var(--text-primary);
}

/* 综合评分卡片 */
.score-card :deep(.el-card__body) {
  padding: 24px;
}

.score-grid {
  display: flex;
  gap: 40px;
  align-items: center;
}

.score-item {
  flex-shrink: 0;
}

.score-inner {
  display: flex;
  flex-direction: column;
  align-items: center;
}

.score-value {
  font-size: 28px;
  font-weight: 700;
  color: var(--primary);
}

.score-label {
  font-size: 12px;
  color: var(--text-muted);
}

.score-details {
  flex: 1;
  display: flex;
  flex-direction: column;
  gap: 16px;
}

.score-row {
  display: flex;
  align-items: center;
  gap: 16px;
}

.score-name {
  width: 150px;
  font-size: 14px;
  color: var(--text-secondary);
}

.sodium-level {
  font-size: 14px;
  color: var(--text-secondary);
  display: flex;
  align-items: center;
  gap: 8px;
}

/* 百分比样式 */
.pct-low { color: #67c23a; font-weight: 600; }
.pct-normal { color: #409eff; font-weight: 600; }
.pct-high { color: #f56c6c; font-weight: 600; }

/* 人群卡片 */
.crowd-section {
  display: flex;
  gap: 40px;
}

.crowd-suitable h5,
.crowd-unsuitable h5 {
  margin: 0 0 12px;
  font-size: 15px;
  color: var(--text-primary);
}

.crowd-tag {
  margin-right: 8px;
  margin-bottom: 8px;
}

/* 报告样式 */
.nutrition-report {
  padding: 22px;
  background: var(--success-soft);
  border: 1px solid rgba(34, 197, 94, 0.12);
  border-radius: 18px;
}

.nutrition-report.suggestion {
  background: rgba(245, 158, 11, 0.1);
  border-color: rgba(245, 158, 11, 0.16);
}

.nutrition-report.meal-advice {
  background: rgba(64, 158, 255, 0.08);
  border-color: rgba(64, 158, 255, 0.2);
}

.nutrition-report h4 {
  margin: 0 0 12px;
  color: var(--text-primary);
  font-size: 18px;
}

.nutrition-report p {
  margin: 0;
  line-height: 1.9;
  color: var(--text-secondary);
  white-space: pre-wrap;
}

@media (max-width: 768px) {
  .form-card :deep(.el-card__body),
  .table-card :deep(.el-card__body) {
    padding: 18px;
  }

  .nutrition-report {
    padding: 18px;
  }

  .score-grid {
    flex-direction: column;
    gap: 20px;
  }

  .score-row {
    flex-direction: column;
    align-items: flex-start;
    gap: 8px;
  }

  .crowd-section {
    flex-direction: column;
    gap: 20px;
  }
}
</style>
