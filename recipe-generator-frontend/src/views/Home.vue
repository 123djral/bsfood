<template>
  <div class="home page-shell">
    <section class="hero-section">
      <div class="hero-copy">
        <span class="hero-badge">AI 智能膳食助手</span>
        <h2 class="page-title">让食材、偏好与营养分析协同工作</h2>
        <p class="page-subtitle">
          从食材识别到个性化食谱生成，再到营养评估与管理，一个页面体系内完成更流畅的烹饪决策体验。
        </p>
      </div>

      <div class="hero-carousel">
        <el-carousel :interval="5000" type="card" height="360px">
          <el-carousel-item v-for="item in carouselItems" :key="item.id">
            <div class="carousel-item">
              <span class="carousel-tag">0{{ item.id }}</span>
              <h3>{{ item.title }}</h3>
              <p>{{ item.description }}</p>
            </div>
          </el-carousel-item>
        </el-carousel>
      </div>
    </section>

    <section class="features-section">
      <div class="section-heading">
        <h3 class="section-title">核心功能模块</h3>
        <p class="section-subtitle">保留现有业务流程，只提升页面结构、视觉节奏与操作体验。</p>
      </div>

      <el-row :gutter="20" class="feature-grid">
        <el-col :xs="24" :sm="12" :lg="6">
          <el-card shadow="hover" class="feature-card">
            <template #header>
              <div class="card-header">
                <span>食材管理</span>
              </div>
            </template>
            <div class="card-content">
              <p>支持文本与图像双模态食材输入，实现食材自动分类、存储与智能替代推荐。</p>
              <el-button type="primary" @click="navigateTo('/food')">开始使用</el-button>
            </div>
          </el-card>
        </el-col>
        <el-col :xs="24" :sm="12" :lg="6">
          <el-card shadow="hover" class="feature-card">
            <template #header>
              <div class="card-header">
                <span>用户画像</span>
              </div>
            </template>
            <div class="card-content">
              <p>收集用户基础信息、健康目标、饮食限制、口味倾向与烹饪熟练度，支持画像实时更新。</p>
              <el-button type="primary" @click="navigateTo('/user')">开始使用</el-button>
            </div>
          </el-card>
        </el-col>
        <el-col :xs="24" :sm="12" :lg="6">
          <el-card shadow="hover" class="feature-card">
            <template #header>
              <div class="card-header">
                <span>食谱生成</span>
              </div>
            </template>
            <div class="card-content">
              <p>根据用户现有食材与画像生成 3-5 个候选食谱，并按熟练度动态调整步骤详细度。</p>
              <el-button type="primary" @click="navigateTo('/recipe')">开始使用</el-button>
            </div>
          </el-card>
        </el-col>
        <el-col :xs="24" :sm="12" :lg="6">
          <el-card shadow="hover" class="feature-card">
            <template #header>
              <div class="card-header">
                <span>营养分析</span>
              </div>
            </template>
            <div class="card-content">
              <p>计算核心营养指标并生成符合《中国居民膳食指南》的评估报告与建议。</p>
              <el-button type="primary" @click="navigateTo('/nutrition')">开始使用</el-button>
            </div>
          </el-card>
        </el-col>
      </el-row>
    </section>
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
  display: flex;
  flex-direction: column;
  gap: 34px;
}

.hero-section {
  display: grid;
  grid-template-columns: minmax(0, 1fr);
  gap: 24px;
  padding: 32px;
  border: 1px solid rgba(255, 255, 255, 0.72);
  border-radius: 30px;
  background:
    linear-gradient(135deg, rgba(255, 255, 255, 0.96), rgba(239, 246, 255, 0.9)),
    #fff;
  box-shadow: 0 24px 50px rgba(15, 23, 42, 0.08);
}

.hero-copy {
  max-width: 760px;
}

.hero-badge {
  display: inline-flex;
  align-items: center;
  padding: 7px 12px;
  border-radius: 999px;
  background: rgba(37, 99, 235, 0.1);
  color: #1d4ed8;
  font-size: 13px;
  font-weight: 600;
}

.hero-carousel :deep(.el-carousel__container) {
  overflow: visible;
}

.carousel-item {
  height: 100%;
  display: flex;
  flex-direction: column;
  justify-content: center;
  gap: 14px;
  padding: 32px;
  border-radius: 24px;
  background: linear-gradient(160deg, #ffffff, #eff6ff);
  border: 1px solid rgba(191, 219, 254, 0.9);
  box-shadow: 0 18px 36px rgba(37, 99, 235, 0.12);
}

.carousel-tag {
  display: inline-flex;
  width: fit-content;
  padding: 6px 10px;
  border-radius: 999px;
  background: rgba(37, 99, 235, 0.08);
  color: #2563eb;
  font-size: 12px;
  font-weight: 700;
  letter-spacing: 0.08em;
}

.carousel-item h3 {
  margin: 0;
  font-size: 28px;
  color: var(--text-primary);
}

.carousel-item p {
  margin: 0;
  font-size: 15px;
  line-height: 1.8;
  color: var(--text-secondary);
  max-width: 520px;
}

.features-section {
  display: flex;
  flex-direction: column;
  gap: 18px;
}

.section-heading {
  display: flex;
  flex-direction: column;
  gap: 4px;
}

.feature-grid {
  row-gap: 20px;
}

.feature-card {
  height: 100%;
}

.feature-card :deep(.el-card__header) {
  padding: 20px 22px 0;
  border-bottom: none;
}

.feature-card :deep(.el-card__body) {
  padding: 16px 22px 22px;
}

.card-header {
  display: flex;
  align-items: center;
  justify-content: space-between;
  font-size: 18px;
  font-weight: 700;
  color: var(--text-primary);
}

.card-content {
  display: flex;
  flex-direction: column;
  height: 100%;
}

.card-content p {
  flex: 1;
  margin: 0 0 20px;
  color: var(--text-secondary);
  line-height: 1.75;
}

.card-content .el-button {
  width: 100%;
}

@media (max-width: 768px) {
  .hero-section {
    padding: 20px;
    border-radius: 22px;
  }

  .carousel-item {
    padding: 22px;
    border-radius: 18px;
  }

  .carousel-item h3 {
    font-size: 22px;
  }
}
</style>
