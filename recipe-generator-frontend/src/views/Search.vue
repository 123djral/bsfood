<template>
  <div class="search-recipe page-shell">
    <div class="page-header">
      <div>
        <h2 class="page-title">食谱搜索</h2>
        <p class="page-subtitle">输入任意食谱名称，查看完整食材、厨具、烹饪步骤与营养分析。</p>
      </div>
    </div>

    <el-card shadow="hover" class="form-card">
      <el-form label-width="100px">
        <el-form-item label="食谱名称">
          <el-input
            v-model="recipeName"
            placeholder="例如：红烧肉、番茄炒蛋、鱼香肉丝"
            @keyup.enter="searchRecipe"
            style="width: 100%;"
          />
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="searchRecipe" :loading="loading">搜索食谱</el-button>
          <el-button @click="reset">重置</el-button>
        </el-form-item>
      </el-form>
    </el-card>

    <div v-if="recipeData" class="recipe-result">
      <div class="section-heading">
        <h3 class="section-title">{{ recipeData.name }} - 食谱详情</h3>
      </div>

      <!-- 基本信息 -->
      <el-card shadow="hover" class="info-card">
        <el-descriptions :column="3" border>
          <el-descriptions-item label="烹饪时间">{{ recipeData.cookingTime }} 分钟</el-descriptions-item>
          <el-descriptions-item label="难度等级">{{ recipeData.difficultyLevel }}</el-descriptions-item>
          <el-descriptions-item label="菜系">{{ recipeData.cuisineStyle }}</el-descriptions-item>
          <el-descriptions-item label="口味">{{ recipeData.flavorProfile }}</el-descriptions-item>
          <el-descriptions-item label="英文名">{{ recipeData.englishName || '-' }}</el-descriptions-item>
          <el-descriptions-item label="适宜人群">
            <el-tag v-for="crowd in (recipeData.suitableCrowd || [])" :key="crowd" size="small" type="success" style="margin-right: 4px;">{{ crowd }}</el-tag>
          </el-descriptions-item>
        </el-descriptions>
      </el-card>

      <!-- 食材列表 -->
      <el-card shadow="hover" class="detail-card">
        <template #header>
          <div class="card-header"><span>🥗 食材清单</span></div>
        </template>
        <div class="tag-list">
          <el-tag v-for="ing in (recipeData.ingredients || [])" :key="ing" type="info" class="ingredient-tag">{{ ing }}</el-tag>
        </div>
      </el-card>

      <!-- 厨具列表 -->
      <el-card shadow="hover" class="detail-card">
        <template #header>
          <div class="card-header"><span>🍳 所需厨具</span></div>
        </template>
        <div class="tag-list">
          <el-tag v-for="tool in (recipeData.tools || [])" :key="tool" type="warning" class="tool-tag">{{ tool }}</el-tag>
        </div>
      </el-card>

      <!-- 烹饪步骤 -->
      <el-card shadow="hover" class="detail-card">
        <template #header>
          <div class="card-header"><span>📝 烹饪步骤</span></div>
        </template>
        <div class="steps-list">
          <div v-for="(step, idx) in parsedSteps" :key="idx" class="step-item">
            <div class="step-number">{{ idx + 1 }}</div>
            <div class="step-text">{{ step }}</div>
          </div>
        </div>
      </el-card>

      <!-- 营养分析 -->
      <div v-if="nutritionData" class="section-heading">
        <h3 class="section-title">🥦 营养分析</h3>
      </div>

      <div v-if="nutritionData" class="nutrition-data">
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
                <span v-if="scope.row.dailyPct" :class="'pct-' + getPctLevel(scope.row.dailyPct)">{{ scope.row.dailyPct }}%</span>
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
                <span v-if="scope.row.dailyPct" :class="'pct-' + getPctLevel(scope.row.dailyPct)">{{ scope.row.dailyPct }}%</span>
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
  </div>
</template>

<script>
import { recipeApi } from '../api/index.js'

export default {
  name: 'Search',
  data() {
    return {
      recipeName: '',
      loading: false,
      recipeData: null,
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
  computed: {
    parsedSteps() {
      if (!this.recipeData?.steps) return []
      const steps = this.recipeData.steps
      // 支持多种分隔符：1. xxx 2. xxx 或 1 xxx | 2 xxx
      const parts = steps.split(/(?:①|1\.)/).filter(s => s.trim())
      return parts.map(s => s.replace(/^\d+[.)、]\s*/, '').trim()).filter(s => s)
    }
  },
  methods: {
    async searchRecipe() {
      if (!this.recipeName.trim()) {
        this.$message.warning('请输入食谱名称')
        return
      }
      this.loading = true
      this.recipeData = null
      this.nutritionData = null
      try {
        const res = await recipeApi.search(this.recipeName.trim())
        if (res.code === 200) {
          this.recipeData = res.data
          this.parseNutritionData(res.data.nutritionData)
          this.$message.success('查询成功')
        } else {
          this.$message.error(res.message || '查询失败')
        }
      } catch (e) {
        this.$message.error('查询失败，请重试')
      } finally {
        this.loading = false
      }
    },
    parseNutritionData(data) {
      if (!data) return
      this.nutritionData = data
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
    },
    reset() {
      this.recipeName = ''
      this.recipeData = null
      this.nutritionData = null
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
.search-recipe {
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
.table-card :deep(.el-card__body),
.info-card :deep(.el-card__body),
.detail-card :deep(.el-card__body) {
  padding: 24px;
}

.table-card :deep(.el-card__header),
.info-card :deep(.el-card__header),
.detail-card :deep(.el-card__header),
.score-card :deep(.el-card__header),
.crowd-card :deep(.el-card__header) {
  padding: 20px 24px 0;
  border-bottom: none;
}

.recipe-result {
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

.tag-list {
  display: flex;
  flex-wrap: wrap;
  gap: 8px;
}

.ingredient-tag,
.tool-tag {
  font-size: 14px;
  padding: 6px 12px;
}

.steps-list {
  display: flex;
  flex-direction: column;
  gap: 16px;
}

.step-item {
  display: flex;
  gap: 16px;
  align-items: flex-start;
}

.step-number {
  width: 32px;
  height: 32px;
  border-radius: 50%;
  background: var(--primary);
  color: #fff;
  display: flex;
  align-items: center;
  justify-content: center;
  font-weight: 700;
  font-size: 14px;
  flex-shrink: 0;
}

.step-text {
  flex: 1;
  line-height: 1.8;
  color: var(--text-secondary);
  padding-top: 4px;
}

.nutrition-data {
  display: flex;
  flex-direction: column;
  gap: 16px;
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
