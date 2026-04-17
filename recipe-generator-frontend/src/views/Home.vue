<template>
  <div class="home page-shell">
    <!-- Hero Section -->
    <section class="hero-section">
      <div class="hero-bg-pattern"></div>
      <div class="hero-content">
        <div class="hero-text">
          <div class="hero-badge">
            <span class="badge-icon">🍽️</span>
            <span>AI 智能膳食管家</span>
          </div>
          <h1 class="hero-title">您的专属美食定制专家</h1>
          <p class="hero-desc">
            基于先进的大模型技术，根据您的口味偏好、营养需求与现有食材，<br>
            为您量身打造个性化食谱，让每一餐都成为美味与健康的完美结合。
          </p>
          <div class="hero-actions">
            <el-button type="primary" size="large" @click="navigateTo('/food')">
              <span class="btn-icon">🥬</span> 开始管理食材
            </el-button>
            <el-button size="large" @click="navigateTo('/recipe')">
              <span class="btn-icon">👨‍🍳</span> 生成食谱
            </el-button>
          </div>
        </div>
        <div class="hero-visual">
          <div class="food-illustration">
            <div class="food-circle circle-1">🥗</div>
            <div class="food-circle circle-2">🍳</div>
            <div class="food-circle circle-3">🥩</div>
            <div class="food-circle circle-4">🥦</div>
            <div class="food-circle circle-5">🍜</div>
            <div class="food-circle circle-6">🥐</div>
          </div>
        </div>
      </div>
    </section>

    <!-- Stats Section -->
    <section class="stats-section">
      <div class="stats-grid">
        <div class="stat-card">
          <div class="stat-icon">📸</div>
          <div class="stat-value">AI</div>
          <div class="stat-label">智能识别</div>
        </div>
        <div class="stat-card">
          <div class="stat-icon">🎯</div>
          <div class="stat-value">个性化</div>
          <div class="stat-label">精准匹配</div>
        </div>
        <div class="stat-card">
          <div class="stat-icon">📊</div>
          <div class="stat-value">科学</div>
          <div class="stat-label">营养分析</div>
        </div>
        <div class="stat-card">
          <div class="stat-icon">🔄</div>
          <div class="stat-value">智能</div>
          <div class="stat-label">食材替换</div>
        </div>
      </div>
    </section>

    <!-- Features Section -->
    <section class="features-section">
      <div class="section-header">
        <h2 class="section-title">核心功能</h2>
        <p class="section-desc">一站式解决您的饮食需求，从食材到餐桌的全程智能服务</p>
      </div>

      <div class="features-grid">
        <div class="feature-card feature-card-1" @click="navigateTo('/food')">
          <div class="feature-icon">🥬</div>
          <div class="feature-content">
            <h3>智能食材管理</h3>
            <p>支持文本、图像多模态输入，AI自动识别食材并分类存储。智能检测重复食材，自动累计计算数量。</p>
            <div class="feature-tags">
              <span class="tag">图像识别</span>
              <span class="tag">智能分类</span>
              <span class="tag">保质期提醒</span>
            </div>
          </div>
          <div class="feature-arrow">→</div>
        </div>

        <div class="feature-card feature-card-2" @click="navigateTo('/user')">
          <div class="feature-icon">👤</div>
          <div class="feature-content">
            <h3>个性化用户画像</h3>
            <p>收集您的健康目标、饮食限制、口味偏好与烹饪水平，生成精准的用户画像档案。</p>
            <div class="feature-tags">
              <span class="tag">健康目标</span>
              <span class="tag">饮食限制</span>
              <span class="tag">口味偏好</span>
            </div>
          </div>
          <div class="feature-arrow">→</div>
        </div>

        <div class="feature-card feature-card-3" @click="navigateTo('/recipe')">
          <div class="feature-icon">👨‍🍳</div>
          <div class="feature-content">
            <h3>AI 食谱生成</h3>
            <p>基于您的食材与偏好，智能生成3-5道专属食谱。根据烹饪水平调整步骤详细度，告别选择困难。</p>
            <div class="feature-tags">
              <span class="tag">智能匹配</span>
              <span class="tag">难度可选</span>
              <span class="tag">秒级生成</span>
            </div>
          </div>
          <div class="feature-arrow">→</div>
        </div>

        <div class="feature-card feature-card-4" @click="navigateTo('/nutrition')">
          <div class="feature-icon">📊</div>
          <div class="feature-content">
            <h3>科学营养分析</h3>
            <p>深度分析食谱营养成分，生成符合《中国居民膳食指南》的评估报告与改善建议。</p>
            <div class="feature-tags">
              <span class="tag">热量计算</span>
              <span class="tag">营养均衡</span>
              <span class="tag">专业建议</span>
            </div>
          </div>
          <div class="feature-arrow">→</div>
        </div>
      </div>
    </section>

    <!-- Process Section -->
    <section class="process-section">
      <div class="section-header">
        <h2 class="section-title">使用流程</h2>
        <p class="section-desc">简单四步，开启您的专属美食之旅</p>
      </div>

      <div class="process-steps">
        <div class="process-step">
          <div class="step-number">01</div>
          <div class="step-content">
            <h4>设置偏好</h4>
            <p>在个人中心设置您的健康目标、饮食限制与口味偏好</p>
          </div>
          <div class="step-icon">⚙️</div>
        </div>
        <div class="step-connector"></div>
        <div class="process-step">
          <div class="step-number">02</div>
          <div class="step-content">
            <h4>添加食材</h4>
            <p>通过文本或图像输入您的现有食材，AI自动识别分类</p>
          </div>
          <div class="step-icon">📥</div>
        </div>
        <div class="step-connector"></div>
        <div class="process-step">
          <div class="step-number">03</div>
          <div class="step-content">
            <h4>生成食谱</h4>
            <p>AI根据食材与偏好生成个性化食谱，可收藏喜欢的食谱</p>
          </div>
          <div class="step-icon">🍳</div>
        </div>
        <div class="step-connector"></div>
        <div class="process-step">
          <div class="step-number">04</div>
          <div class="step-content">
            <h4>营养分析</h4>
            <p>对食谱进行深度营养分析，获得科学饮食建议</p>
          </div>
          <div class="step-icon">📈</div>
        </div>
      </div>
    </section>

    <!-- CTA Section -->
    <section class="cta-section">
      <div class="cta-content">
        <h2>准备好开启您的专属美食之旅了吗？</h2>
        <p>基于大模型技术，为您打造个性化的美食体验</p>
        <el-button type="primary" size="large" @click="navigateTo('/food')">
          立即开始 →
        </el-button>
      </div>
    </section>
  </div>
</template>

<script>
export default {
  name: 'Home',
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
  gap: 48px;
  padding-bottom: 20px;
}

/* Hero Section */
.hero-section {
  position: relative;
  padding: 48px;
  border-radius: 32px;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 50%, #f093fb 100%);
  overflow: hidden;
}

.hero-bg-pattern {
  position: absolute;
  inset: 0;
  background-image:
    radial-gradient(circle at 20% 80%, rgba(255,255,255,0.1) 0%, transparent 50%),
    radial-gradient(circle at 80% 20%, rgba(255,255,255,0.08) 0%, transparent 40%);
}

.hero-content {
  position: relative;
  display: flex;
  align-items: center;
  gap: 48px;
}

.hero-text {
  flex: 1;
  color: #fff;
}

.hero-badge {
  display: inline-flex;
  align-items: center;
  gap: 8px;
  padding: 8px 16px;
  border-radius: 999px;
  background: rgba(255,255,255,0.2);
  backdrop-filter: blur(10px);
  font-size: 14px;
  font-weight: 600;
  margin-bottom: 20px;
}

.badge-icon {
  font-size: 18px;
}

.hero-title {
  margin: 0 0 16px;
  font-size: 42px;
  font-weight: 800;
  line-height: 1.2;
  text-shadow: 0 2px 20px rgba(0,0,0,0.1);
}

.hero-desc {
  margin: 0 0 28px;
  font-size: 16px;
  line-height: 1.8;
  opacity: 0.95;
}

.hero-actions {
  display: flex;
  gap: 16px;
}

.hero-actions .el-button {
  padding: 14px 28px;
  font-size: 15px;
  font-weight: 600;
  border-radius: 14px;
  border: none;
  transition: all 0.3s ease;
}

.hero-actions .el-button--primary {
  background: #fff;
  color: #667eea;
  box-shadow: 0 8px 24px rgba(0,0,0,0.15);
}

.hero-actions .el-button--primary:hover {
  transform: translateY(-2px);
  box-shadow: 0 12px 32px rgba(0,0,0,0.2);
}

.hero-actions .el-button:not(.el-button--primary) {
  background: rgba(255,255,255,0.15);
  color: #fff;
  border: 1px solid rgba(255,255,255,0.3);
}

.btn-icon {
  margin-right: 6px;
}

.hero-visual {
  flex-shrink: 0;
}

.food-illustration {
  position: relative;
  width: 280px;
  height: 280px;
}

.food-circle {
  position: absolute;
  width: 72px;
  height: 72px;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 36px;
  background: rgba(255,255,255,0.95);
  border-radius: 50%;
  box-shadow: 0 8px 32px rgba(0,0,0,0.15);
  animation: float 3s ease-in-out infinite;
}

.circle-1 { top: 0; left: 50%; transform: translateX(-50%); animation-delay: 0s; }
.circle-2 { top: 50%; right: 0; transform: translateY(-50%); animation-delay: 0.5s; }
.circle-3 { bottom: 0; right: 15%; animation-delay: 1s; }
.circle-4 { bottom: 0; left: 15%; animation-delay: 1.5s; }
.circle-5 { top: 50%; left: 0; transform: translateY(-50%); animation-delay: 2s; }
.circle-6 { top: 20%; left: 20%; animation-delay: 2.5s; }

@keyframes float {
  0%, 100% { transform: translateY(0); }
  50% { transform: translateY(-10px); }
}

.circle-1 { animation: float-top 3s ease-in-out infinite; }
@keyframes float-top {
  0%, 100% { transform: translateX(-50%) translateY(0); }
  50% { transform: translateX(-50%) translateY(-10px); }
}

/* Stats Section */
.stats-section {
  margin-top: -24px;
  padding: 0 24px;
}

.stats-grid {
  display: grid;
  grid-template-columns: repeat(4, 1fr);
  gap: 20px;
  max-width: 1000px;
  margin: 0 auto;
}

.stat-card {
  padding: 24px 20px;
  text-align: center;
  background: #fff;
  border-radius: 20px;
  box-shadow: 0 8px 30px rgba(15,23,42,0.08);
  border: 1px solid rgba(148,163,184,0.12);
  transition: all 0.3s ease;
}

.stat-card:hover {
  transform: translateY(-4px);
  box-shadow: 0 16px 40px rgba(15,23,42,0.12);
}

.stat-icon {
  font-size: 32px;
  margin-bottom: 8px;
}

.stat-value {
  font-size: 20px;
  font-weight: 700;
  color: var(--text-primary);
  margin-bottom: 4px;
}

.stat-label {
  font-size: 13px;
  color: var(--text-muted);
}

/* Features Section */
.features-section {
  padding: 0 24px;
}

.section-header {
  text-align: center;
  margin-bottom: 32px;
}

.section-title {
  margin: 0 0 10px;
  font-size: 32px;
  font-weight: 700;
  color: var(--text-primary);
}

.section-desc {
  margin: 0;
  font-size: 15px;
  color: var(--text-secondary);
}

.features-grid {
  display: grid;
  grid-template-columns: repeat(2, 1fr);
  gap: 24px;
  max-width: 1000px;
  margin: 0 auto;
}

.feature-card {
  display: flex;
  align-items: flex-start;
  gap: 20px;
  padding: 28px;
  background: #fff;
  border-radius: 24px;
  border: 1px solid rgba(148,163,184,0.15);
  box-shadow: 0 4px 20px rgba(15,23,42,0.06);
  cursor: pointer;
  transition: all 0.3s ease;
  position: relative;
  overflow: hidden;
}

.feature-card::before {
  content: '';
  position: absolute;
  top: 0;
  left: 0;
  width: 4px;
  height: 100%;
  transition: width 0.3s ease;
}

.feature-card-1::before { background: linear-gradient(180deg, #667eea, #764ba2); }
.feature-card-2::before { background: linear-gradient(180deg, #f093fb, #f5576c); }
.feature-card-3::before { background: linear-gradient(180deg, #4facfe, #00f2fe); }
.feature-card-4::before { background: linear-gradient(180deg, #43e97b, #38f9d7); }

.feature-card:hover {
  transform: translateY(-4px);
  box-shadow: 0 16px 48px rgba(15,23,42,0.12);
}

.feature-card:hover::before {
  width: 6px;
}

.feature-icon {
  flex-shrink: 0;
  width: 64px;
  height: 64px;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 32px;
  background: var(--bg-soft);
  border-radius: 16px;
}

.feature-content {
  flex: 1;
}

.feature-content h3 {
  margin: 0 0 8px;
  font-size: 18px;
  font-weight: 700;
  color: var(--text-primary);
}

.feature-content p {
  margin: 0 0 12px;
  font-size: 14px;
  line-height: 1.7;
  color: var(--text-secondary);
}

.feature-tags {
  display: flex;
  flex-wrap: wrap;
  gap: 8px;
}

.tag {
  padding: 4px 10px;
  font-size: 12px;
  background: var(--bg-soft);
  color: var(--primary);
  border-radius: 6px;
  font-weight: 500;
}

.feature-arrow {
  flex-shrink: 0;
  font-size: 20px;
  color: var(--text-muted);
  transition: all 0.3s ease;
  align-self: center;
}

.feature-card:hover .feature-arrow {
  color: var(--primary);
  transform: translateX(4px);
}

/* Process Section */
.process-section {
  padding: 0 24px;
}

.process-steps {
  display: flex;
  align-items: flex-start;
  justify-content: center;
  gap: 0;
  max-width: 1100px;
  margin: 0 auto;
}

.process-step {
  flex: 1;
  max-width: 220px;
  padding: 24px;
  background: #fff;
  border-radius: 20px;
  border: 1px solid rgba(148,163,184,0.15);
  box-shadow: 0 4px 20px rgba(15,23,42,0.05);
  text-align: center;
  position: relative;
}

.step-number {
  width: 48px;
  height: 48px;
  margin: 0 auto 16px;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 16px;
  font-weight: 800;
  color: #fff;
  background: linear-gradient(135deg, #667eea, #764ba2);
  border-radius: 50%;
}

.step-content h4 {
  margin: 0 0 8px;
  font-size: 16px;
  font-weight: 700;
  color: var(--text-primary);
}

.step-content p {
  margin: 0;
  font-size: 13px;
  line-height: 1.6;
  color: var(--text-secondary);
}

.step-icon {
  margin-top: 12px;
  font-size: 24px;
}

.step-connector {
  width: 60px;
  height: 2px;
  background: linear-gradient(90deg, #667eea, #764ba2);
  margin-top: 48px;
  flex-shrink: 0;
}

/* CTA Section */
.cta-section {
  padding: 48px;
  border-radius: 32px;
  background: linear-gradient(135deg, #f093fb 0%, #f5576c 100%);
  text-align: center;
}

.cta-content h2 {
  margin: 0 0 10px;
  font-size: 28px;
  font-weight: 700;
  color: #fff;
}

.cta-content p {
  margin: 0 0 24px;
  font-size: 15px;
  color: rgba(255,255,255,0.9);
}

.cta-content .el-button {
  padding: 14px 32px;
  font-size: 15px;
  font-weight: 600;
  background: #fff;
  color: #f5576c;
  border: none;
  border-radius: 12px;
  box-shadow: 0 8px 24px rgba(0,0,0,0.15);
}

.cta-content .el-button:hover {
  transform: translateY(-2px);
  box-shadow: 0 12px 32px rgba(0,0,0,0.2);
}

/* Responsive */
@media (max-width: 900px) {
  .hero-content {
    flex-direction: column;
    text-align: center;
  }

  .hero-actions {
    justify-content: center;
  }

  .hero-title {
    font-size: 32px;
  }

  .hero-visual {
    display: none;
  }

  .stats-grid {
    grid-template-columns: repeat(2, 1fr);
  }

  .features-grid {
    grid-template-columns: 1fr;
  }

  .process-steps {
    flex-direction: column;
    align-items: center;
    gap: 16px;
  }

  .step-connector {
    width: 2px;
    height: 30px;
    margin: 0;
  }
}

@media (max-width: 600px) {
  .hero-section {
    padding: 32px 24px;
    border-radius: 24px;
  }

  .hero-title {
    font-size: 26px;
  }

  .hero-actions {
    flex-direction: column;
  }

  .stats-section,
  .features-section,
  .process-section {
    padding: 0 12px;
  }

  .cta-section {
    padding: 32px 24px;
    border-radius: 24px;
  }

  .section-title {
    font-size: 26px;
  }
}
</style>
