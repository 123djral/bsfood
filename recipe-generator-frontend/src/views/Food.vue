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
            <el-radio-group v-model="inputType" class="input-type-group" @change="handleInputTypeChange">
              <el-radio-button label="text">文本输入</el-radio-button>
              <el-radio-button label="image">图像输入</el-radio-button>
              <el-radio-button label="mix">混合输入</el-radio-button>
            </el-radio-group>

            <div v-if="inputType === 'text' || inputType === 'mix'">
              <el-input
                v-model="textInput"
                type="textarea"
                :rows="4"
                placeholder="识别结果会自动回填到这里，也可手动补充食材名称"
                class="text-block"
              ></el-input>
            </div>

            <div v-if="inputType === 'image' || inputType === 'mix'" class="upload-block">
              <el-upload
                class="ingredient-upload"
                drag
                action="#"
                :auto-upload="false"
                :show-file-list="false"
                accept="image/*"
                :before-upload="beforeImageUpload"
                :on-change="handleImageChange"
              >
                <el-icon class="upload-icon"><UploadFilled /></el-icon>
                <div class="el-upload__text">上传食材图片后将自动识别并回填食材名称</div>
                <template #tip>
                  <div class="upload-tip">支持 JPG、PNG、WEBP，单张不超过 5MB</div>
                </template>
              </el-upload>

              <div v-if="uploadedImageName" class="upload-meta">
                <span>已选择：{{ uploadedImageName }}</span>
                <el-button text @click="clearImage">移除</el-button>
              </div>
            </div>

            <el-button type="primary" @click="recognizeFood" :loading="recognizing">识别食材</el-button>

            <div v-if="recognizedList.length > 0" class="result-section">
              <div class="result-header">
                <h4 class="section-title small-title">识别结果</h4>
                <p class="result-summary">已自动去重并回填：{{ recognizedNamesText }}</p>
              </div>
              <el-table :data="recognizedList" style="width: 100%">
                <el-table-column prop="name" label="食材名称"></el-table-column>
                <el-table-column prop="type" label="类别"></el-table-column>
                <el-table-column prop="quantity" label="数量(g)"></el-table-column>
                <el-table-column prop="shelfLife" label="保质期(天)"></el-table-column>
                <el-table-column label="操作" width="100">
                  <template #default="scope">
                    <el-button type="warning" size="small" @click="openSubstituteDialog(scope.row, scope.$index)">替换</el-button>
                  </template>
                </el-table-column>
              </el-table>
            </div>
          </el-tab-pane>

          <el-tab-pane label="食材列表" name="list">
            <div class="list-header">
              <el-input v-model="searchKeyword" placeholder="搜索食材名称" style="width: 200px;" clearable @clear="loadFoodList" @keyup.enter="loadFoodList">
                <template #prefix><el-icon><Search /></el-icon></template>
              </el-input>
              <el-button type="primary" size="small" @click="loadFoodList" class="refresh-button">搜索</el-button>
              <el-button type="danger" size="small" @click="deleteSelectedFoods" :disabled="selectedFoods.length === 0">
                删除选中 ({{ selectedFoods.length }})
              </el-button>
            </div>
            <el-table :data="foodList" style="width: 100%" v-loading="listLoading" @selection-change="handleSelectionChange">
              <el-table-column type="selection" width="50"></el-table-column>
              <el-table-column prop="name" label="食材名称"></el-table-column>
              <el-table-column label="数量" width="120">
                <template #default="scope">
                  {{ scope.row.quantity ? `大约 ${scope.row.quantity}g` : '-' }}
                </template>
              </el-table-column>
              <el-table-column prop="type" label="类别"></el-table-column>
              <el-table-column label="保质期" width="120">
                <template #default="scope">
                  {{ scope.row.shelfLife ? `大约 ${scope.row.shelfLife} 天` : '-' }}
                </template>
              </el-table-column>
              <el-table-column label="操作" width="150">
                <template #default="scope">
                  <el-button type="primary" size="small" @click="getSubstitute(scope.row)">替代推荐</el-button>
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
          </el-tab-pane>
        </el-tabs>
      </div>
    </el-card>

    <el-dialog v-model="substituteVisible" title="替代食材推荐" width="500px">
      <el-table :data="substituteList" v-loading="substituteLoading" @row-click="selectSubstitute">
        <el-table-column prop="name" label="食材名称"></el-table-column>
        <el-table-column prop="type" label="类别"></el-table-column>
        <el-table-column prop="quantity" label="建议用量(g)"></el-table-column>
      </el-table>
    </el-dialog>

    <el-dialog v-model="replaceDialogVisible" title="替换食材" width="500px">
      <div class="replace-origin">
        <span class="replace-label">原食材：</span>
        <span class="replace-value">{{ substituteOriginal?.name }} ({{ substituteOriginal?.type }}，约{{ substituteOriginal?.quantity }}g)</span>
      </div>
      <el-divider />
      <div class="replace-section">
        <h4 class="replace-subtitle">从推荐列表选择：</h4>
        <el-table :data="substituteList" v-loading="substituteLoading" size="small" highlight-current-row @row-click="selectSubstituteFromList">
          <el-table-column prop="name" label="食材名称"></el-table-column>
          <el-table-column prop="type" label="类别"></el-table-column>
          <el-table-column prop="quantity" label="建议用量(g)"></el-table-column>
        </el-table>
      </div>
      <el-divider>或</el-divider>
      <div class="replace-section">
        <h4 class="replace-subtitle">自行输入：</h4>
        <el-input v-model="customReplaceName" placeholder="输入替换食材名称"></el-input>
        <div class="custom-replace-btns">
          <el-button type="primary" size="small" @click="confirmCustomReplace">确认替换</el-button>
          <el-button size="small" @click="replaceDialogVisible = false">取消</el-button>
        </div>
      </div>
    </el-dialog>
  </div>
</template>

<script>
import { UploadFilled } from '@element-plus/icons-vue'
import { Search } from '@element-plus/icons-vue'
import { foodApi } from '../api/index.js'

const INVALID_INGREDIENT_NAMES = ['食材', '配料', '原料', '材料', '图片', '照片', '盘子', '碗', '桌子', '厨房', '背景']

export default {
  name: 'Food',
  components: {
    UploadFilled,
    Search
  },
  data() {
    return {
      activeTab: 'input',
      inputType: 'text',
      textInput: '',
      imageDesc: '',
      recognizing: false,
      recognizedList: [],
      foodList: [],
      selectedFoods: [],  // 存储选中的食材
      listLoading: false,
      substituteVisible: false,
      substituteList: [],
      substituteLoading: false,
      uploadedImageName: '',
      replaceDialogVisible: false,
      substituteOriginal: null,
      substituteIndex: null,
      customReplaceName: '',
      searchKeyword: '',
      currentPage: 1,
      pageSize: 10,
      totalCount: 0
    }
  },
  computed: {
    recognizedNamesText() {
      return this.recognizedList.map(item => item.name).join('、')
    }
  },
  mounted() {
    this.loadFoodList()
  },
  methods: {
    handleInputTypeChange(type) {
      if (type === 'text') {
        this.clearImage()
      }
    },
    beforeImageUpload(file) {
      const isImage = file.type.startsWith('image/')
      const isLt5M = file.size / 1024 / 1024 < 5
      if (!isImage) {
        this.$message.warning('请上传图片文件')
        return false
      }
      if (!isLt5M) {
        this.$message.warning('图片大小不能超过 5MB')
        return false
      }
      return false
    },
    async handleImageChange(file) {
      const rawFile = file.raw
      if (!rawFile) return
      if (!rawFile.type.startsWith('image/')) {
        this.$message.warning('请上传图片文件')
        return
      }
      if (rawFile.size / 1024 / 1024 >= 5) {
        this.$message.warning('图片大小不能超过 5MB')
        return
      }
      this.uploadedImageName = rawFile.name
      try {
        this.imageDesc = await this.readFileAsDataUrlWithCompression(rawFile)
        await this.recognizeFood(true)
      } catch (e) {
        this.$message.error('图片读取失败，请重新上传')
      }
    },
    // 压缩图片并转为 Base64
    readFileAsDataUrlWithCompression(file, maxWidth = 800, quality = 0.8) {
      return new Promise((resolve, reject) => {
        const reader = new FileReader()
        reader.onload = (e) => {
          const img = new Image()
          img.onload = () => {
            // 计算压缩后的尺寸
            let width = img.width
            let height = img.height
            if (width > maxWidth) {
              height = (height * maxWidth) / width
              width = maxWidth
            }
            // 绘制压缩后的图片
            const canvas = document.createElement('canvas')
            canvas.width = width
            canvas.height = height
            const ctx = canvas.getContext('2d')
            ctx.drawImage(img, 0, 0, width, height)
            // 转为 Base64 (使用 image/jpeg 格式体积更小)
            const dataUrl = canvas.toDataURL('image/jpeg', quality)
            resolve(dataUrl)
          }
          img.onerror = () => reject(new Error('image-load-failed'))
          img.src = e.target.result
        }
        reader.onerror = () => reject(new Error('read-failed'))
        reader.readAsDataURL(file)
      })
    },
    readFileAsDataUrl(file) {
      return new Promise((resolve, reject) => {
        const reader = new FileReader()
        reader.onload = () => resolve(reader.result)
        reader.onerror = () => reject(new Error('read-failed'))
        reader.readAsDataURL(file)
      })
    },
    clearImage() {
      this.imageDesc = ''
      this.uploadedImageName = ''
    },
    normalizeIngredientName(name) {
      return String(name || '')
        .replace(/[：:，,。；;、/\\|（）()\[\]{}<>"'`]/g, ' ')
        .replace(/\s+/g, ' ')
        .trim()
    },
    isValidIngredientName(name) {
      if (!name) return false
      if (name.length > 12) return false
      if (/\d/.test(name)) return false
      return !INVALID_INGREDIENT_NAMES.some(item => name.includes(item))
    },
    normalizeRecognizedFoods(foodList) {
      const map = new Map()
      ;(foodList || []).forEach(food => {
        const name = this.normalizeIngredientName(food?.name)
        if (!this.isValidIngredientName(name)) return
        if (map.has(name)) {
          map.get(name).quantity += (typeof food?.quantity === 'number' ? food.quantity : 100)
        } else {
          map.set(name, {
            ...food,
            name,
            type: food?.type || '其他',
            quantity: typeof food?.quantity === 'number' ? food.quantity : 100,
            shelfLife: Number.isInteger(food?.shelfLife) ? food.shelfLife : 7
          })
        }
      })
      return Array.from(map.values())
    },
    mergeIngredientText(names) {
      const existing = this.textInput
        .split(/[,，、\n]/)
        .map(item => this.normalizeIngredientName(item))
        .filter(item => this.isValidIngredientName(item))
      const merged = Array.from(new Set([...existing, ...names]))
      return merged.join('、')
    },
    async recognizeFood(autoTriggered = false) {
      const needsText = this.inputType === 'text'
      const needsImage = this.inputType === 'image'
      const needsMix = this.inputType === 'mix'

      if ((needsText || needsMix) && !this.textInput.trim() && !this.imageDesc) {
        this.$message.warning('请输入食材信息或上传图片')
        return
      }
      if (needsImage && !this.imageDesc) {
        this.$message.warning('请先上传食材图片')
        return
      }
      if (needsMix && !this.imageDesc && !this.textInput.trim()) {
        this.$message.warning('请输入食材信息或上传图片')
        return
      }

      this.recognizing = true
      try {
        const userId = this.getCurrentUserId()
        const res = await foodApi.recognize(this.textInput, this.imageDesc, this.inputType, userId)
        if (res.code === 200) {
          const normalizedFoods = this.normalizeRecognizedFoods(res.data?.foodList || [])
          if (normalizedFoods.length === 0) {
            this.$message.warning('没有识别到有效食材，请换一张图片或补充文字说明')
            return
          }
          this.recognizedList = normalizedFoods
          this.textInput = this.mergeIngredientText(normalizedFoods.map(item => item.name))
          this.$message.success(autoTriggered ? '图片识别完成，已自动回填食材' : '识别成功')
          this.loadFoodList()
        } else {
          this.$message.error(res.message || '识别失败，请重试')
        }
      } catch (e) {
        this.$message.error('识别失败，请稍后重试')
      } finally {
        this.recognizing = false
      }
    },
    async loadFoodList() {
      this.listLoading = true
      try {
        const userId = this.getCurrentUserId()
        const res = await foodApi.list(userId, this.searchKeyword || null, this.currentPage, this.pageSize)
        if (res.code === 200) {
          this.foodList = res.data
          this.totalCount = res.total || 0
        }
      } catch (e) {
        console.error('加载食材列表失败:', e)
      } finally {
        this.listLoading = false
      }
    },
    handlePageChange(page) {
      this.currentPage = page
      this.loadFoodList()
    },
    getCurrentUserId() {
      // 优先从 localStorage 获取 userId
      const userId = localStorage.getItem('userId')
      if (userId) {
        return parseInt(userId, 10)
      }
      // 如果没有，返回 null 或默认用户ID
      return null
    },
    handleSelectionChange(selection) {
      this.selectedFoods = selection
    },
    async deleteSelectedFoods() {
      if (this.selectedFoods.length === 0) {
        this.$message.warning('请先选择要删除的食材')
        return
      }
      try {
        await this.$confirm(`确定要删除选中的 ${this.selectedFoods.length} 个食材吗？`, '提示', { type: 'warning' })
        const userId = this.getCurrentUserId()
        let successCount = 0
        for (const food of this.selectedFoods) {
          const res = await foodApi.delete(food.id, userId)
          if (res.code === 200) {
            successCount++
          }
        }
        this.$message.success(`成功删除 ${successCount} 个食材`)
        this.selectedFoods = []
        this.loadFoodList()
      } catch {
        // cancelled
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
    },
    async openSubstituteDialog(food, index) {
      this.substituteOriginal = food
      this.substituteIndex = index
      this.customReplaceName = ''
      this.replaceDialogVisible = true
      this.substituteLoading = true
      try {
        const res = await foodApi.substituteByName(food.name, food.type)
        if (res.code === 200) {
          this.substituteList = res.data || []
        }
      } catch (e) {
        this.substituteList = []
      } finally {
        this.substituteLoading = false
      }
    },
    selectSubstituteFromList(row) {
      this.applySubstitute(row)
    },
    confirmCustomReplace() {
      if (!this.customReplaceName.trim()) {
        this.$message.warning('请输入替换食材名称')
        return
      }
      this.applySubstitute({ name: this.customReplaceName.trim(), type: '其他', quantity: 100 })
    },
    applySubstitute(substitute) {
      if (this.substituteIndex === null) return
      const normalized = this.normalizeIngredientName(substitute.name)
      if (!this.isValidIngredientName(normalized)) {
        this.$message.warning('替换食材名称无效')
        return
      }
      const existing = this.recognizedList.findIndex((f, i) => i !== this.substituteIndex && this.normalizeIngredientName(f.name) === normalized)
      if (existing >= 0) {
        this.recognizedList[existing].quantity += substitute.quantity || 100
        this.recognizedList.splice(this.substituteIndex, 1)
        this.$message.success(`已替换为"${normalized}"，数量已累计`)
      } else {
        this.recognizedList.splice(this.substituteIndex, 1, {
          ...this.substituteOriginal,
          name: normalized,
          type: substitute.type || '其他',
          quantity: substitute.quantity || 100
        })
        this.$message.success(`已替换为"${normalized}"`)
      }
      this.replaceDialogVisible = false
      this.textInput = this.mergeIngredientText(this.recognizedList.map(f => f.name))
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

.text-block,
.upload-block {
  margin-bottom: 20px;
}

.ingredient-upload {
  width: 100%;
}

.ingredient-upload :deep(.el-upload-dragger) {
  width: 100%;
  border-radius: 16px;
  border: 1px dashed rgba(37, 99, 235, 0.28);
  background: rgba(37, 99, 235, 0.04);
}

.upload-icon {
  font-size: 28px;
  color: var(--primary);
  margin-bottom: 8px;
}

.upload-tip {
  margin-top: 8px;
  color: var(--text-muted);
}

.upload-meta {
  display: flex;
  align-items: center;
  justify-content: space-between;
  gap: 12px;
  margin-top: 10px;
  padding: 10px 14px;
  border-radius: 12px;
  background: #f8fafc;
  color: var(--text-secondary);
}

.result-section {
  margin-top: 24px;
}

.result-header {
  display: flex;
  flex-direction: column;
  gap: 4px;
  margin-bottom: 14px;
}

.result-summary {
  margin: 0;
  color: var(--text-secondary);
  line-height: 1.7;
}

.small-title {
  margin-bottom: 0;
  font-size: 18px;
}

.refresh-button {
  margin-bottom: 16px;
}

.list-header {
  display: flex;
  gap: 10px;
  margin-bottom: 10px;
}

.replace-origin {
  padding: 8px 0;
  color: var(--text-secondary);
}

.replace-label {
  font-weight: 600;
  color: var(--text-primary);
}

.replace-subtitle {
  margin: 0 0 10px;
  font-size: 14px;
  color: var(--text-secondary);
}

.replace-section {
  margin-bottom: 10px;
}

.custom-replace-btns {
  margin-top: 12px;
  display: flex;
  gap: 10px;
}

@media (max-width: 768px) {
  .content-card :deep(.el-card__header) {
    padding: 18px 18px 0;
  }

  .content-card :deep(.el-card__body) {
    padding: 16px 18px 18px;
  }

  .upload-meta {
    flex-direction: column;
    align-items: flex-start;
  }
}
</style>
