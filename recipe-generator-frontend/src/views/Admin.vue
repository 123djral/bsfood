<template>
  <div class="admin page-shell">
    <div class="page-header">
      <div>
        <h2 class="page-title">管理后台</h2>
        <p class="page-subtitle">集中查看用户、食材与食谱数据，保持现有管理逻辑不变，仅优化信息密度与视觉层次。</p>
      </div>
    </div>

    <el-card shadow="hover" class="admin-card">
      <el-tabs v-model="activeTab" @tab-click="handleTabClick">
        <el-tab-pane label="用户管理" name="user">
          <el-table :data="userList" style="width: 100%" v-loading="loading">
            <el-table-column prop="id" label="用户ID" width="80"></el-table-column>
            <el-table-column prop="username" label="用户名"></el-table-column>
            <el-table-column prop="age" label="年龄" width="80"></el-table-column>
            <el-table-column prop="gender" label="性别" width="80"></el-table-column>
            <el-table-column prop="phone" label="手机号码"></el-table-column>
            <el-table-column prop="createTime" label="创建时间"></el-table-column>
            <el-table-column label="操作" width="100">
              <template #default="scope">
                <el-button size="small" type="danger" @click="deleteUser(scope.row.id)">删除</el-button>
              </template>
            </el-table-column>
          </el-table>
        </el-tab-pane>

        <el-tab-pane label="食材管理" name="food">
          <el-table :data="foodList" style="width: 100%" v-loading="loading">
            <el-table-column prop="id" label="ID" width="80"></el-table-column>
            <el-table-column prop="name" label="食材名称"></el-table-column>
            <el-table-column prop="type" label="类别" width="120"></el-table-column>
            <el-table-column prop="quantity" label="数量(g)"></el-table-column>
            <el-table-column prop="shelfLife" label="保质期(天)" width="100"></el-table-column>
            <el-table-column prop="createTime" label="创建时间"></el-table-column>
            <el-table-column label="操作" width="100">
              <template #default="scope">
                <el-button size="small" type="danger" @click="deleteFood(scope.row.id)">删除</el-button>
              </template>
            </el-table-column>
          </el-table>
        </el-tab-pane>

        <el-tab-pane label="食谱管理" name="recipe">
          <el-table :data="recipeList" style="width: 100%" v-loading="loading">
            <el-table-column prop="id" label="ID" width="80"></el-table-column>
            <el-table-column prop="name" label="食谱名称"></el-table-column>
            <el-table-column prop="userId" label="用户ID" width="80"></el-table-column>
            <el-table-column prop="cookingTime" label="烹饪时间(分钟)" width="120"></el-table-column>
            <el-table-column prop="difficultyLevel" label="难度" width="80"></el-table-column>
            <el-table-column prop="collectCount" label="收藏数" width="80"></el-table-column>
            <el-table-column label="操作" width="100">
              <template #default="scope">
                <el-button size="small" type="danger" @click="deleteRecipe(scope.row.id)">删除</el-button>
              </template>
            </el-table-column>
          </el-table>
        </el-tab-pane>
      </el-tabs>
    </el-card>
  </div>
</template>

<script>
import { adminApi } from '../api/index.js'

export default {
  name: 'Admin',
  data() {
    return {
      activeTab: 'user',
      loading: false,
      userList: [],
      foodList: [],
      recipeList: []
    }
  },
  mounted() {
    this.loadUsers()
  },
  methods: {
    handleTabClick(tab) {
      if (tab.paneName === 'user') this.loadUsers()
      else if (tab.paneName === 'food') this.loadFoods()
      else if (tab.paneName === 'recipe') this.loadRecipes()
    },
    async loadUsers() {
      this.loading = true
      try {
        const res = await adminApi.userList()
        if (res.code === 200) this.userList = res.data || []
      } catch (e) {
        this.$message.error('加载用户列表失败')
      } finally {
        this.loading = false
      }
    },
    async loadFoods() {
      this.loading = true
      try {
        const res = await adminApi.foodList()
        if (res.code === 200) this.foodList = res.data || []
      } catch (e) {
        this.$message.error('加载食材列表失败')
      } finally {
        this.loading = false
      }
    },
    async loadRecipes() {
      this.loading = true
      try {
        const res = await adminApi.recipeList()
        if (res.code === 200) this.recipeList = res.data || []
      } catch (e) {
        this.$message.error('加载食谱列表失败')
      } finally {
        this.loading = false
      }
    },
    async deleteUser(id) {
      try {
        await this.$confirm('确定要删除该用户吗？', '提示', { type: 'warning' })
        const res = await adminApi.deleteUser(id)
        if (res.code === 200) {
          this.$message.success('删除成功')
          this.loadUsers()
        }
      } catch { /* cancelled */ }
    },
    async deleteFood(id) {
      try {
        await this.$confirm('确定要删除该食材吗？', '提示', { type: 'warning' })
        const res = await adminApi.deleteFood(id)
        if (res.code === 200) {
          this.$message.success('删除成功')
          this.loadFoods()
        }
      } catch { /* cancelled */ }
    },
    async deleteRecipe(id) {
      try {
        await this.$confirm('确定要删除该食谱吗？', '提示', { type: 'warning' })
        const res = await adminApi.deleteRecipe(id)
        if (res.code === 200) {
          this.$message.success('删除成功')
          this.loadRecipes()
        }
      } catch { /* cancelled */ }
    }
  }
}
</script>

<style scoped>
.admin {
  display: flex;
  flex-direction: column;
  gap: 22px;
}

.page-header {
  display: flex;
  flex-direction: column;
  gap: 4px;
}

.admin-card :deep(.el-card__body) {
  padding: 22px 24px 24px;
}

@media (max-width: 768px) {
  .admin-card :deep(.el-card__body) {
    padding: 18px;
  }
}
</style>
