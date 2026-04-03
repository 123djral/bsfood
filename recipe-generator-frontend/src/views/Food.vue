<template>
  <div class="food">
    <el-card shadow="hover">
      <template #header>
        <div class="card-header">
          <span>食材管理</span>
        </div>
      </template>
      <div class="card-content">
        <el-tabs v-model="activeTab">
          <el-tab-pane label="食材输入">
            <el-radio-group v-model="inputType" style="margin-bottom: 20px;">
              <el-radio-button label="text">文本输入</el-radio-button>
              <el-radio-button label="image">图像输入</el-radio-button>
              <el-radio-button label="mix">混合输入</el-radio-button>
            </el-radio-group>
            
            <div v-if="inputType === 'text'">
              <el-input
                type="textarea"
                :rows="4"
                placeholder="请输入食材信息，格式：食材 1, 数量 1; 食材 2, 数量 2"
                v-model="textInput"
              ></el-input>
            </div>
            
            <div v-else-if="inputType === 'image'">
              <el-upload
                class="upload-demo"
                action="#"
                :auto-upload="false"
                :on-change="handleImageChange"
                :limit="1"
                :file-list="fileList"
              >
                <el-button type="primary">点击上传</el-button>
                <template #tip>
                  <div class="el-upload__tip">
                    请上传食材图像
                  </div>
                </template>
              </el-upload>
            </div>
            
            <div v-else-if="inputType === 'mix'">
              <el-input
                type="textarea"
                :rows="4"
                placeholder="请输入食材信息，格式：食材 1, 数量 1; 食材 2, 数量 2"
                v-model="textInput"
                style="margin-bottom: 20px;"
              ></el-input>
              <el-upload
                class="upload-demo"
                action="#"
                :auto-upload="false"
                :on-change="handleImageChange"
                :limit="1"
                :file-list="fileList"
              >
                <el-button type="primary">点击上传</el-button>
                <template #tip>
                  <div class="el-upload__tip">
                    请上传食材图像
                  </div>
                </template>
              </el-upload>
            </div>
            
            <el-button type="primary" style="margin-top: 20px;" @click="recognizeFood">识别食材</el-button>
          </el-tab-pane>
          
          <el-tab-pane label="食材列表">
            <el-table :data="foodList" style="width: 100%">
              <el-table-column prop="id" label="ID" width="80"></el-table-column>
              <el-table-column prop="name" label="食材名称"></el-table-column>
              <el-table-column prop="quantity" label="数量"></el-table-column>
              <el-table-column prop="unit" label="单位"></el-table-column>
              <el-table-column prop="type" label="类别"></el-table-column>
              <el-table-column prop="confidence" label="置信度" width="100"></el-table-column>
              <el-table-column label="操作" width="150">
                <template #default="scope">
                  <el-button type="primary" size="small" @click="editFood(scope.row)">编辑</el-button>
                  <el-button type="danger" size="small" @click="deleteFood(scope.row.id)">删除</el-button>
                </template>
              </el-table-column>
            </el-table>
          </el-tab-pane>
        </el-tabs>
      </div>
    </el-card>
  </div>
</template>

<script>
export default {
  name: 'Food',
  data() {
    return {
      activeTab: '0',
      inputType: 'text',
      textInput: '',
      fileList: [],
      foodList: [
        {
          id: 1,
          name: '西红柿',
          quantity: 200.0,
          unit: 'g',
          type: '蔬菜',
          confidence: 0.95
        },
        {
          id: 2,
          name: '鸡蛋',
          quantity: 2.0,
          unit: '个',
          type: '蛋类',
          confidence: 0.98
        },
        {
          id: 3,
          name: '青菜',
          quantity: 100.0,
          unit: 'g',
          type: '蔬菜',
          confidence: 0.92
        }
      ]
    }
  },
  methods: {
    handleImageChange(file, fileList) {
      this.fileList = fileList
    },
    recognizeFood() {
      // 调用食材识别接口
      this.$axios.post('/api/food/recognize', {
        text: this.textInput,
        type: this.inputType
      }).then(response => {
        if (response.data.code === 200) {
          this.foodList = response.data.data.foodList
          this.$message.success('识别成功')
        } else {
          this.$message.error(response.data.message)
        }
      }).catch(error => {
        console.error('识别失败:', error)
        this.$message.error('识别失败，请重试')
      })
    },
    editFood(row) {
      // 编辑食材
      this.$message.info('编辑食材功能开发中')
    },
    deleteFood(id) {
      // 删除食材
      this.$confirm('确定要删除这个食材吗？', '提示', {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning'
      }).then(() => {
        this.foodList = this.foodList.filter(item => item.id !== id)
        this.$message.success('删除成功')
      }).catch(() => {
        this.$message.info('已取消删除')
      })
    }
  }
}
</script>

<style scoped>
.food {
  padding: 20px;
}

.card-header {
  display: flex;
  justify-content: center;
  align-items: center;
}

.card-content {
  padding: 20px;
}

.upload-demo {
  border: 1px dashed #d9d9d9;
  border-radius: 6px;
  padding: 20px;
  text-align: center;
}
</style>