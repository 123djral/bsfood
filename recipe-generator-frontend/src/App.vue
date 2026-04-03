<template>
  <div id="app">
    <el-container>
      <el-header height="60px">
        <div class="header-content">
          <h1 class="logo">基于大模型的个性化食谱生成系统</h1>
          <el-menu v-if="currentUser" mode="horizontal" :default-active="$route.path" router class="nav-menu">
            <el-menu-item index="/">首页</el-menu-item>
            <el-menu-item index="/food">食材管理</el-menu-item>
            <el-menu-item index="/recipe">食谱生成</el-menu-item>
            <el-menu-item index="/nutrition">营养分析</el-menu-item>
            <el-menu-item index="/user">个人中心</el-menu-item>
            <el-menu-item index="/admin">管理后台</el-menu-item>
          </el-menu>
          <div v-if="currentUser" class="user-info">
            <span>{{ currentUser.username }}</span>
            <el-button type="text" @click="logout">退出</el-button>
          </div>
        </div>
      </el-header>
      <el-main>
        <router-view></router-view>
      </el-main>
      <el-footer height="40px">
        <div class="footer-content">
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
  font-family: Avenir, Helvetica, Arial, sans-serif;
  -webkit-font-smoothing: antialiased;
  -moz-osx-font-smoothing: grayscale;
  color: #2c3e50;
}

.header-content {
  display: flex;
  align-items: center;
  height: 100%;
  gap: 20px;
}

.logo {
  margin: 0;
  font-size: 18px;
  color: #333;
  white-space: nowrap;
}

.nav-menu {
  flex: 1;
  border-bottom: none !important;
}

.user-info {
  white-space: nowrap;
  display: flex;
  align-items: center;
  gap: 8px;
  font-size: 14px;
  color: #666;
}

.footer-content {
  display: flex;
  justify-content: center;
  align-items: center;
  height: 100%;
}

.footer-content p {
  margin: 0;
  font-size: 14px;
  color: #666;
}
</style>
