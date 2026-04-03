<template>
  <div class="admin">
    <h2>管理员页面</h2>
    <el-tabs v-model="activeTab">
      <el-tab-pane label="用户管理" name="user">
        <el-table :data="userList" style="width: 100%">
          <el-table-column prop="id" label="用户ID" width="80"></el-table-column>
          <el-table-column prop="username" label="用户名"></el-table-column>
          <el-table-column prop="age" label="年龄" width="80"></el-table-column>
          <el-table-column prop="gender" label="性别" width="80"></el-table-column>
          <el-table-column prop="phone" label="手机号码"></el-table-column>
          <el-table-column prop="createTime" label="创建时间"></el-table-column>
          <el-table-column label="操作" width="150">
            <template #default="scope">
              <el-button size="small" @click="editUser(scope.row.id)">编辑</el-button>
              <el-button size="small" type="danger" @click="deleteUser(scope.row.id)">删除</el-button>
            </template>
          </el-table-column>
        </el-table>
      </el-tab-pane>
      <el-tab-pane label="食材管理" name="food">
        <el-table :data="foodList" style="width: 100%">
          <el-table-column prop="id" label="食材ID" width="80"></el-table-column>
          <el-table-column prop="name" label="食材名称"></el-table-column>
          <el-table-column prop="type" label="食材类别" width="120"></el-table-column>
          <el-table-column prop="quantity" label="食材数量"></el-table-column>
          <el-table-column prop="shelfLife" label="保质期（天）" width="120"></el-table-column>
          <el-table-column prop="createTime" label="创建时间"></el-table-column>
          <el-table-column label="操作" width="150">
            <template #default="scope">
              <el-button size="small" @click="editFood(scope.row.id)">编辑</el-button>
              <el-button size="small" type="danger" @click="deleteFood(scope.row.id)">删除</el-button>
            </template>
          </el-table-column>
        </el-table>
      </el-tab-pane>
      <el-tab-pane label="食谱管理" name="recipe">
        <el-table :data="recipeList" style="width: 100%">
          <el-table-column prop="id" label="食谱ID" width="80"></el-table-column>
          <el-table-column prop="name" label="食谱名称"></el-table-column>
          <el-table-column prop="userId" label="用户ID" width="80"></el-table-column>
          <el-table-column prop="cookingTime" label="烹饪时间（分钟）" width="120"></el-table-column>
          <el-table-column prop="difficultyLevel" label="难度等级" width="100"></el-table-column>
          <el-table-column prop="collectCount" label="收藏次数" width="100"></el-table-column>
          <el-table-column prop="createTime" label="创建时间"></el-table-column>
          <el-table-column label="操作" width="150">
            <template #default="scope">
              <el-button size="small" @click="editRecipe(scope.row.id)">编辑</el-button>
              <el-button size="small" type="danger" @click="deleteRecipe(scope.row.id)">删除</el-button>
            </template>
          </el-table-column>
        </el-table>
      </el-tab-pane>
    </el-tabs>
  </div>
</template>

<script>
export default {
  name: 'Admin',
  data() {
    return {
      activeTab: 'user',
      userList: [
        { id: 1, username: 'user1', age: 25, gender: '男', phone: '13800138001', createTime: '2026-01-01' },
        { id: 2, username: 'user2', age: 30, gender: '女', phone: '13800138002', createTime: '2026-01-02' },
        { id: 3, username: 'user3', age: 35, gender: '男', phone: '13800138003', createTime: '2026-01-03' }
      ],
      foodList: [
        { id: 1, name: '鸡蛋', type: '蛋类', quantity: 10, shelfLife: 30, createTime: '2026-01-01' },
        { id: 2, name: '西红柿', type: '蔬菜', quantity: 5, shelfLife: 7, createTime: '2026-01-02' },
        { id: 3, name: '黄瓜', type: '蔬菜', quantity: 8, shelfLife: 7, createTime: '2026-01-03' }
      ],
      recipeList: [
        { id: 1, name: '西红柿炒鸡蛋', userId: 1, cookingTime: 15, difficultyLevel: '简单', collectCount: 120, createTime: '2026-01-01' },
        { id: 2, name: '黄瓜炒鸡肉', userId: 2, cookingTime: 20, difficultyLevel: '中等', collectCount: 80, createTime: '2026-01-02' },
        { id: 3, name: '牛肉炖西红柿', userId: 3, cookingTime: 30, difficultyLevel: '困难', collectCount: 150, createTime: '2026-01-03' }
      ]
    }
  },
  methods: {
    editUser(id) {
      // 编辑用户
      this.$message.info('编辑用户: ' + id)
    },
    deleteUser(id) {
      // 删除用户
      this.$message.success('删除用户: ' + id)
    },
    editFood(id) {
      // 编辑食材
      this.$message.info('编辑食材: ' + id)
    },
    deleteFood(id) {
      // 删除食材
      this.$message.success('删除食材: ' + id)
    },
    editRecipe(id) {
      // 编辑食谱
      this.$message.info('编辑食谱: ' + id)
    },
    deleteRecipe(id) {
      // 删除食谱
      this.$message.success('删除食谱: ' + id)
    }
  }
}
</script>

<style scoped>
.admin {
  padding: 20px;
}

.admin h2 {
  margin-bottom: 20px;
  color: #333;
}

.el-tabs {
  margin-top: 20px;
}
</style>