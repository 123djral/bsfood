<template>
  <div class="nutrition">
    <h2>营养分析</h2>
    <el-form :model="nutritionForm" label-width="100px">
      <el-form-item label="食材列表">
        <el-select v-model="nutritionForm.foodIds" multiple placeholder="请选择食材">
          <el-option v-for="food in foodList" :key="food.id" :label="food.name" :value="food.id"></el-option>
        </el-select>
      </el-form-item>
      <el-form-item>
        <el-button type="primary" @click="analyzeNutrition">分析营养</el-button>
      </el-form-item>
    </el-form>
    
    <div v-if="nutritionData" class="nutrition-data">
      <h3>营养分析结果</h3>
      <el-card shadow="hover">
        <template #header>
          <div class="card-header">
            <span>营养成分</span>
          </div>
        </template>
        <div class="card-content">
          <el-table :data="nutritionTableData" style="width: 100%">
            <el-table-column prop="name" label="营养成分" width="180"></el-table-column>
            <el-table-column prop="value" label="含量"></el-table-column>
            <el-table-column prop="unit" label="单位"></el-table-column>
            <el-table-column prop="status" label="状态">
              <template #default="scope">
                <el-tag :type="scope.row.status === '正常' ? 'success' : 'warning'">{{ scope.row.status }}</el-tag>
              </template>
            </el-table-column>
          </el-table>
        </div>
      </el-card>
      
      <div class="nutrition-report">
        <h4>营养评估报告</h4>
        <p>{{ nutritionReport }}</p>
      </div>
    </div>
  </div>
</template>

<script>
export default {
  name: 'Nutrition',
  data() {
    return {
      nutritionForm: {
        foodIds: []
      },
      foodList: [
        { id: 1, name: '鸡蛋' },
        { id: 2, name: '西红柿' },
        { id: 3, name: '黄瓜' },
        { id: 4, name: '鸡肉' },
        { id: 5, name: '牛肉' }
      ],
      nutritionData: null,
      nutritionTableData: [],
      nutritionReport: ''
    }
  },
  methods: {
    analyzeNutrition() {
      // 调用后端 API 分析营养
      // 这里使用模拟数据
      this.nutritionData = {
        calorie: 500,
        protein: 30,
        fat: 20,
        carbohydrate: 60,
        vitamin: '维生素A: 500IU, 维生素C: 30mg',
        mineral: '钙: 200mg, 铁: 5mg'
      }
      
      this.nutritionTableData = [
        { name: '热量', value: this.nutritionData.calorie, unit: '大卡', status: '正常' },
        { name: '蛋白质', value: this.nutritionData.protein, unit: 'g', status: '正常' },
        { name: '脂肪', value: this.nutritionData.fat, unit: 'g', status: '正常' },
        { name: '碳水化合物', value: this.nutritionData.carbohydrate, unit: 'g', status: '正常' },
        { name: '维生素', value: this.nutritionData.vitamin, unit: '', status: '正常' },
        { name: '矿物质', value: this.nutritionData.mineral, unit: '', status: '正常' }
      ]
      
      this.nutritionReport = '根据《中国居民膳食指南》，您的营养摄入基本符合标准。建议适当增加蔬菜和水果的摄入量，以获取更多的维生素和矿物质。'
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
  align-items: center;
}

.card-content {
  padding: 20px;
}

.nutrition-report {
  margin-top: 30px;
  padding: 20px;
  background-color: #f5f5f5;
  border-radius: 4px;
}

.nutrition-report h4 {
  margin-bottom: 10px;
  color: #333;
}

.nutrition-report p {
  line-height: 1.5;
  color: #666;
}
</style>