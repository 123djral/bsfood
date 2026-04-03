<template>
  <div class="home">
    <el-carousel :interval="5000" type="card" height="400px">
      <el-carousel-item v-for="item in carouselItems" :key="item.id">
        <div class="carousel-item">
          <h3>{{ item.title }}</h3>
          <p>{{ item.description }}</p>
        </div>
      </el-carousel-item>
    </el-carousel>
    
    <div class="features">
      <el-row :gutter="20">
        <el-col :span="6">
          <el-card shadow="hover">
            <template #header>
              <div class="card-header">
                <span>食材管理</span>
              </div>
            </template>
            <div class="card-content">
              <p>支持文本与图像双模态食材输入，实现食材自动分类、存储与智能替代推荐</p>
              <el-button type="primary" @click="navigateTo('/food')">开始使用</el-button>
            </div>
          </el-card>
        </el-col>
        <el-col :span="6">
          <el-card shadow="hover">
            <template #header>
              <div class="card-header">
                <span>用户画像</span>
              </div>
            </template>
            <div class="card-content">
              <p>收集用户基础信息、健康目标、饮食限制、口味倾向与烹饪熟练度，支持画像实时更新</p>
              <el-button type="primary" @click="navigateTo('/user')">开始使用</el-button>
            </div>
          </el-card>
        </el-col>
        <el-col :span="6">
          <el-card shadow="hover">
            <template #header>
              <div class="card-header">
                <span>食谱生成</span>
              </div>
            </template>
            <div class="card-content">
              <p>根据用户现有食材与画像生成3-5个候选食谱，按烹饪熟练度调整步骤详细度，支持食谱收藏功能</p>
              <el-button type="primary" @click="navigateTo('/recipe')">开始使用</el-button>
            </div>
          </el-card>
        </el-col>
        <el-col :span="6">
          <el-card shadow="hover">
            <template #header>
              <div class="card-header">
                <span>营养分析</span>
              </div>
            </template>
            <div class="card-content">
              <p>计算28项核心营养指标，生成符合《中国居民膳食指南》的评估报告，提供可落地的营养调整建议</p>
              <el-button type="primary" @click="navigateTo('/nutrition')">开始使用</el-button>
            </div>
          </el-card>
        </el-col>
      </el-row>
    </div>
  </div>
</template>

<script>
export default {
  name: 'Home',
  data() {
    return {
      carouselItems: [
        {
          id: 1,
          title: '个性化食谱生成',
          description: '基于大模型技术，根据您的食材和偏好生成专属食谱'
        },
        {
          id: 2,
          title: '多模态食材识别',
          description: '支持文本和图像输入，精准识别食材信息'
        },
        {
          id: 3,
          title: '科学营养分析',
          description: '生成符合《中国居民膳食指南》的营养评估报告'
        },
        {
          id: 4,
          title: '智能食材替代',
          description: '当食材不足时，推荐合适的替代方案'
        }
      ]
    }
  },
  methods: {
    navigateTo(path) {
      this.$router.push(path)
    }
  }
}
</script>

<style scoped>
.home {
  padding: 20px;
}

.carousel-item {
  height: 100%;
  display: flex;
  flex-direction: column;
  justify-content: center;
  align-items: center;
  background-color: #f5f5f5;
  border-radius: 4px;
}

.carousel-item h3 {
  font-size: 24px;
  margin-bottom: 16px;
  color: #333;
}

.carousel-item p {
  font-size: 16px;
  color: #666;
  text-align: center;
  padding: 0 40px;
}

.features {
  margin-top: 40px;
}

.card-header {
  display: flex;
  justify-content: center;
  align-items: center;
}

.card-content {
  padding: 20px;
}

.card-content p {
  margin-bottom: 20px;
  line-height: 1.5;
}

.card-content .el-button {
  width: 100%;
}
</style>