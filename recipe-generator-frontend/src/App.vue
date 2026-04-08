<template>
  <div id="app" class="app-shell">
    <el-container class="layout-shell">
      <el-header height="auto" class="app-header">
        <div class="header-content page-shell">
          <div class="brand-block">
            <div class="brand-mark">AI</div>
            <div class="brand-copy">
              <h1 class="logo">个性化食谱生成系统</h1>
              <p class="brand-subtitle">更清晰的食材管理、食谱生成与营养分析体验</p>
            </div>
          </div>

          <el-menu
            v-if="currentUser"
            mode="horizontal"
            :default-active="$route.path"
            router
            class="nav-menu"
          >
            <el-menu-item index="/">首页</el-menu-item>
            <el-menu-item index="/food">食材管理</el-menu-item>
            <el-menu-item index="/recipe">食谱生成</el-menu-item>
            <el-menu-item index="/nutrition">营养分析</el-menu-item>
            <el-menu-item index="/user">个人中心</el-menu-item>
            <el-menu-item index="/admin">管理后台</el-menu-item>
          </el-menu>

          <div v-if="currentUser" class="user-info">
            <div class="user-avatar">{{ currentUser.username?.slice(0, 1)?.toUpperCase() }}</div>
            <div class="user-copy">
              <span class="user-name">{{ currentUser.username }}</span>
              <span class="user-role">当前已登录</span>
            </div>
            <el-button class="logout-button" text @click="logout">退出</el-button>
          </div>
        </div>
      </el-header>

      <el-main class="app-main">
        <div class="main-content page-shell">
          <router-view></router-view>
        </div>
      </el-main>

      <el-footer height="56px" class="app-footer">
        <div class="footer-content page-shell">
          <p>&copy; 2026 基于大模型的个性化食谱生成系统</p>
        </div>
      </el-footer>
    </el-container>
  </div>
</template>

<script>
export default {
  name: 'App',
  computed: {
    currentUser() {
      return JSON.parse(localStorage.getItem('user') || 'null')
    }
  },
  methods: {
    logout() {
      localStorage.removeItem('user')
      this.$router.push('/login')
    }
  }
}
</script>

<style>
#app {
  min-height: 100vh;
  color: var(--text-primary);
  -webkit-font-smoothing: antialiased;
  -moz-osx-font-smoothing: grayscale;
}

.layout-shell {
  min-height: 100vh;
  background: transparent;
}

.app-header {
  position: sticky;
  top: 0;
  z-index: 20;
  padding: 18px 20px 0;
  background: rgba(244, 247, 251, 0.72);
  backdrop-filter: blur(14px);
}

.header-content {
  display: flex;
  align-items: center;
  gap: 20px;
  padding: 16px 22px;
  border: 1px solid rgba(255, 255, 255, 0.7);
  border-radius: 24px;
  background: rgba(255, 255, 255, 0.76);
  box-shadow: 0 14px 34px rgba(15, 23, 42, 0.08);
}

.brand-block {
  display: flex;
  align-items: center;
  gap: 14px;
  min-width: 0;
}

.brand-mark {
  width: 44px;
  height: 44px;
  display: inline-flex;
  align-items: center;
  justify-content: center;
  border-radius: 14px;
  background: linear-gradient(135deg, #2563eb, #4f46e5);
  color: #fff;
  font-weight: 700;
  letter-spacing: 0.04em;
  box-shadow: 0 12px 24px rgba(37, 99, 235, 0.28);
}

.brand-copy {
  min-width: 0;
}

.logo {
  margin: 0;
  font-size: 18px;
  line-height: 1.2;
  color: var(--text-primary);
}

.brand-subtitle {
  margin: 4px 0 0;
  font-size: 13px;
  color: var(--text-secondary);
}

.nav-menu {
  flex: 1;
  min-width: 0;
  background: transparent !important;
  border-bottom: none !important;
}

.nav-menu.el-menu--horizontal > .el-menu-item {
  height: 42px;
  line-height: 42px;
  margin: 0 4px;
  border-radius: 12px;
  color: var(--text-secondary);
  border-bottom: none !important;
}

.nav-menu.el-menu--horizontal > .el-menu-item:hover,
.nav-menu.el-menu--horizontal > .el-menu-item.is-active {
  color: var(--primary);
  background: var(--primary-soft);
}

.user-info {
  display: flex;
  align-items: center;
  gap: 12px;
  padding: 8px 10px;
  border-radius: 16px;
  background: rgba(248, 250, 252, 0.95);
  border: 1px solid var(--border-color);
  white-space: nowrap;
}

.user-avatar {
  width: 36px;
  height: 36px;
  display: inline-flex;
  align-items: center;
  justify-content: center;
  border-radius: 50%;
  background: linear-gradient(135deg, #dbeafe, #e0e7ff);
  color: #1d4ed8;
  font-weight: 700;
}

.user-copy {
  display: flex;
  flex-direction: column;
  gap: 2px;
}

.user-name {
  font-size: 14px;
  font-weight: 600;
  color: var(--text-primary);
}

.user-role {
  font-size: 12px;
  color: var(--text-muted);
}

.logout-button {
  color: var(--text-secondary);
}

.app-main {
  padding: 28px 20px 36px;
}

.main-content {
  min-height: calc(100vh - 180px);
}

.app-footer {
  padding: 0 20px 20px;
  background: transparent;
}

.footer-content {
  display: flex;
  justify-content: center;
  align-items: center;
  height: 100%;
  border-top: 1px solid rgba(148, 163, 184, 0.18);
}

.footer-content p {
  margin: 0;
  font-size: 13px;
  color: var(--text-muted);
}

@media (max-width: 1100px) {
  .header-content {
    flex-wrap: wrap;
  }

  .nav-menu {
    order: 3;
    width: 100%;
  }

  .user-info {
    margin-left: auto;
  }
}

@media (max-width: 768px) {
  .app-header,
  .app-main,
  .app-footer {
    padding-left: 12px;
    padding-right: 12px;
  }

  .header-content {
    padding: 14px;
    border-radius: 18px;
    gap: 14px;
  }

  .brand-mark {
    width: 40px;
    height: 40px;
    border-radius: 12px;
  }

  .brand-subtitle {
    display: none;
  }

  .user-info {
    width: 100%;
    justify-content: space-between;
  }

  .nav-menu.el-menu--horizontal {
    display: flex;
    flex-wrap: wrap;
    row-gap: 8px;
    height: auto;
  }

  .nav-menu.el-menu--horizontal > .el-menu-item {
    padding: 0 12px;
    margin: 0 6px 0 0;
  }

  .main-content {
    min-height: auto;
  }
}
</style>
