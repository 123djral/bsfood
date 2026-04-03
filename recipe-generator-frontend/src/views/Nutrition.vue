<template>
  <div class="nutrition">
    <h2>营养分析</h2>
    <el-form label-width="100px">
      <el-form-item label="选择食谱">
        <el-select v-model="selectedRecipeId" placeholder="请选择要分析的食谱" style="width: 100%;">
          <el-option v-for="r in recipeList" :key="r.id" :label="r.name" :value="r.id"></el-option>
        </el-select>
      </el-form-item>
      <el-form-item>
        <el-button type="primary" @click="analyzeNutrition" :loading="analyzing">分析营养</el-button>
      </el-form-item>
    </el-form>

    <div v-if="nutritionData" class="nutrition-data">
      <h3>营养分析结果</h3>
      <el-card shadow="hover">
        <template #header>
          <div class="card-header"><span>营养成分</span></div>
        </template>
        <el-table :data="nutritionTableData" style="width: 100%">
          <el-table-column prop="name" label="营养成分" width="180"></el-table-column>
          <el-table-column prop="value" label="含量"></el-table-column>
          <el-table-column prop="unit" label="单位" width="80"></el-table-column>
        </el-table>
      </el-card>

      <div v-if="evaluation" class="nutrition-report">
        <h4>营养评估</h4>
        <p>{{ evaluation }}</p>
      </div>

      <div v-if="suggestion" class="nutrition-report suggestion">
        <h4>改善建议</h4>
        <p>{{ suggestion }}</p>
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
      analyzing: false,
      nutritionData: null,
      nutritionTableData: [],
      evaluation: '',
      suggestion: ''
    }
  },
  computed: {
    currentUser() {
      return JSON.parse(localStorage.getItem('user') || 'null')
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
    async loadRecipes() {
      if (!this.currentUser) return
      try {
        const res = await recipeApi.list(this.currentUser.id)
        if (res.code === 200) {
          this.recipeList = res.data || []
        }
      } catch (e) {
        console.error('加载食谱列表失败:', e)
      }
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

          const nd = data.nutritionData || {}
          this.nutritionTableData = [
            { name: '热量', value: nd.calorie, unit: '大卡' },
            { name: '蛋白质', value: nd.protein, unit: 'g' },
            { name: '脂肪', value: nd.fat, unit: 'g' },
            { name: '碳水化合物', value: nd.carbohydrate, unit: 'g' },
            { name: '维生素', value: nd.vitamin, unit: '' },
            { name: '矿物质', value: nd.mineral, unit: '' }
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
    }
  }
}
</script>

<style scoped>
.nutrition {
  padding: 20px;
}
.nutrition h2 {
  margin-bottom: 20px;
  color: #333;
}
.nutrition-data {
  margin-top: 30px;
}
.nutrition-data h3 {
  margin-bottom: 20px;
  color: #333;
}
.card-header {
  display: flex;
  justify-content: center;
}
.nutrition-report {
  margin-top: 20px;
  padding: 20px;
  background-color: #f0f9eb;
  border-radius: 4px;
  border-left: 4px solid #67c23a;
}
.nutrition-report.suggestion {
  background-color: #fdf6ec;
  border-left-color: #e6a23c;
}
.nutrition-report h4 {
  margin-bottom: 10px;
  color: #333;
}
.nutrition-report p {
  line-height: 1.8;
  color: #555;
}
</style>
