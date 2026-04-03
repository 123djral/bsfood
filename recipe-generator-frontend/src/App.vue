<template>
  <div id="app">
    <el-container>
      <el-header height="60px">
        <div class="header-content">
          <h1>基于大模型的个性化食谱生成系统</h1>
        </div>
      </el-header>
      <el-main>
        <router-view></router-view>
      </el-main>
      <el-footer height="40px">
        <div class="footer-content">
          <p>© 2026 基于大模型的个性化食谱生成系统</p>
        </div>
      </el-footer>
    </el-container>
  </div>
</template>

<script>
export default {
  name: 'App'
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
  justify-content: center;
  align-items: center;
  height: 100%;
}

.header-content h1 {
  margin: 0;
  font-size: 24px;
  color: #333;
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