<template>
  <div class="food page-shell">
    <div class="page-header">
      <div>
        <h2 class="page-title">食材管理</h2>
        <p class="page-subtitle">支持文本、图像描述与混合输入，统一查看识别结果与库存列表。</p>
      </div>
    </div>

    <el-card shadow="hover" class="content-card">
      <template #header>
        <div class="card-header">
          <span>食材操作面板</span>
        </div>
      </template>
      <div class="card-content">
        <el-tabs v-model="activeTab">
          <el-tab-pane label="食材输入" name="input">
            <el-radio-group v-model="inputType" class="input-type-group">
              <el-radio-button label="text">文本输入</el-radio-button>
              <el-radio-button label="image">图像输入</el-radio-button>
              <el-radio-button label="mix">混合输入</el-radio-button>
            </el-radio-group>

            <div v-if="inputType === 'text' || inputType === 'mix'">
              <el-input
                v-model="textInput"
                type="textarea"
                :rows="4"
                placeholder="请输入食材信息，格式：食材1, 数量1; 食材2, 数量2"
                class="text-block"
              ></el-input>
            </div>

            <div v-if="inputType === 'image' || inputType === 'mix'">
              <el-input
                v-model="imageDesc"
                type="textarea"
                :rows="3"
                placeholder="请描述图片中的食材内容（如：盘子里有两个西红柿和三个鸡蛋）"
                class="text-block"
              ></el-input>
            </div>

            <el-button type="primary" @click="recognizeFood" :loading="recognizing">识别食材</el-button>

            <div v-if="recognizedList.length > 0" class="result-section">
              <h4 class="section-title small-title">识别结果</h4>
              <el-table :data="recognizedList" style="width: 100%">
                <el-table-column prop="name" label="食材名称"></el-table-column>
                <el-table-column prop="type" label="类别"></el-table-column>
                <el-table-column prop="quantity" label="数量(g)"></el-table-column>
                <el-table-column prop="shelfLife" label="保质期(天)"></el-table-column>
              </el-table>
            </div>
          </el-tab-pane>

          <el-tab-pane label="食材列表" name="list">
            <el-button type="primary" size="small" @click="loadFoodList" class="refresh-button">刷新列表</el-button>
            <el-table :data="foodList" style="width: 100%" v-loading="listLoading">
              <el-table-column prop="id" label="ID" width="80"></el-table-column>
              <el-table-column prop="name" label="食材名称"></el-table-column>
              <el-table-column prop="quantity" label="数量(g)"></el-table-column>
              <el-table-column prop="type" label="类别"></el-table-column>
              <el-table-column prop="shelfLife" label="保质期(天)" width="100"></el-table-column>
              <el-table-column label="操作" width="200">
                <template #default="scope">
                  <el-button type="primary" size="small" @click="getSubstitute(scope.row)">替代推荐</el-button>
                  <el-button type="danger" size="small" @click="deleteFood(scope.row.id)">删除</el-button>
                </template>
              </el-table-column>
            </el-table>
          </el-tab-pane>
        </el-tabs>
      </div>
    </el-card>

    <el-dialog v-model="substituteVisible" title="替代食材推荐" width="500px">
      <el-table :data="substituteList" v-loading="substituteLoading">
        <el-table-column prop="name" label="食材名称"></el-table-column>
        <el-table-column prop="type" label="类别"></el-table-column>
        <el-table-column prop="quantity" label="建议用量(g)"></el-table-column>
      </el-table>
    </el-dialog>
  </div>
</template>

<script>
import { foodApi } from '../api/index.js'

export default {
  name: 'Food',
  data() {
    return {
      activeTab: 'input',
      inputType: 'text',
      textInput: '',
      imageDesc: '',
      recognizing: false,
      recognizedList: [],
      foodList: [],
      listLoading: false,
      substituteVisible: false,
      substituteList: [],
      substituteLoading: false
    }
  },
  mounted() {
    this.loadFoodList()
  },
  methods: {
    async recognizeFood() {
      if (this.inputType === 'text' && !this.textInput) {
        this.$message.warning('请输入食材信息')
        return
      }
      this.recognizing = true
      try {
        const res = await foodApi.recognize(this.textInput, this.imageDesc, this.inputType)
        if (res.code === 200) {
          this.recognizedList = res.data.foodList
          this.$message.success('识别成功')
          this.loadFoodList()
        } else {
          this.$message.error(res.message)
        }
      } catch (e) {
        this.$message.error('识别失败，请重试')
      } finally {
        this.recognizing = false
      }
    },
    async loadFoodList() {
      this.listLoading = true
      try {
        const res = await foodApi.list()
        if (res.code === 200) {
          this.foodList = res.data
        }
      } catch (e) {
        console.error('加载食材列表失败:', e)
      } finally {
        this.listLoading = false
      }
    },
    async deleteFood(id) {
      try {
        await this.$confirm('确定要删除这个食材吗？', '提示', { type: 'warning' })
        const res = await foodApi.delete(id)
        if (res.code === 200) {
          this.$message.success('删除成功')
          this.loadFoodList()
        } else {
          this.$message.error(res.message)
        }
      } catch {
        // cancelled
      }
    },
    async getSubstitute(food) {
      this.substituteVisible = true
      this.substituteLoading = true
      try {
        const res = await foodApi.substitute(food.id)
        if (res.code === 200) {
          this.substituteList = res.data
        }
      } catch (e) {
        this.$message.error('获取替代推荐失败')
      } finally {
        this.substituteLoading = false
      }
    }
  }
}
</script>

<style scoped>
.food {
  display: flex;
  flex-direction: column;
  gap: 22px;
}

.page-header {
  display: flex;
  justify-content: space-between;
  align-items: flex-start;
}

.content-card :deep(.el-card__header) {
  padding: 22px 24px 0;
  border-bottom: none;
}

.content-card :deep(.el-card__body) {
  padding: 18px 24px 24px;
}

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  font-size: 18px;
  font-weight: 700;
  color: var(--text-primary);
}

.card-content {
  padding: 0;
}

.input-type-group {
  margin-bottom: 20px;
}

.text-block {
  margin-bottom: 20px;
}

.result-section {
  margin-top: 24px;
}

.small-title {
  margin-bottom: 14px;
  font-size: 18px;
}

.refresh-button {
  margin-bottom: 16px;
}

@media (max-width: 768px) {
  .content-card :deep(.el-card__header) {
    padding: 18px 18px 0;
  }

  .content-card :deep(.el-card__body) {
    padding: 16px 18px 18px;
  }
}
</style>
