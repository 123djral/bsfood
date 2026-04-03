<template>
  <div class="user">
    <h2>用户管理</h2>
    <el-form :model="userForm" label-width="100px">
      <el-form-item label="用户名">
        <el-input v-model="userForm.username" placeholder="请输入用户名"></el-input>
      </el-form-item>
      <el-form-item label="密码">
        <el-input v-model="userForm.password" type="password" placeholder="请输入密码"></el-input>
      </el-form-item>
      <el-form-item label="年龄">
        <el-input v-model="userForm.age" placeholder="请输入年龄"></el-input>
      </el-form-item>
      <el-form-item label="性别">
        <el-select v-model="userForm.gender" placeholder="请选择性别">
          <el-option label="男" value="男"></el-option>
          <el-option label="女" value="女"></el-option>
        </el-select>
      </el-form-item>
      <el-form-item label="手机号码">
        <el-input v-model="userForm.phone" placeholder="请输入手机号码"></el-input>
      </el-form-item>
      <el-form-item label="健康目标">
        <el-select v-model="userForm.healthGoal" placeholder="请选择健康目标">
          <el-option label="减肥" value="减肥"></el-option>
          <el-option label="增肌" value="增肌"></el-option>
          <el-option label="保持健康" value="保持健康"></el-option>
        </el-select>
      </el-form-item>
      <el-form-item label="饮食限制">
        <el-input v-model="userForm.dietLimit" placeholder="请输入饮食限制"></el-input>
      </el-form-item>
      <el-form-item label="口味倾向">
        <el-input v-model="userForm.tastePreference" placeholder="请输入口味倾向"></el-input>
      </el-form-item>
      <el-form-item label="烹饪熟练度">
        <el-select v-model="userForm.cookingLevel" placeholder="请选择烹饪熟练度">
          <el-option label="新手" value="新手"></el-option>
          <el-option label="熟练" value="熟练"></el-option>
          <el-option label="专业" value="专业"></el-option>
        </el-select>
      </el-form-item>
      <el-form-item>
        <el-button type="primary" @click="saveUser">保存用户信息</el-button>
      </el-form-item>
    </el-form>
  </div>
</template>

<script>
export default {
  name: 'User',
  data() {
    return {
      userForm: {
        username: '',
        password: '',
        age: '',
        gender: '',
        phone: '',
        healthGoal: '',
        dietLimit: '',
        tastePreference: '',
        cookingLevel: ''
      }
    }
  },
  methods: {
    saveUser() {
      // 调用后端 API 保存用户信息
      this.$message.success('保存成功')
    }
  }
}
</script>

<style scoped>
.user {
  padding: 20px;
}

.user h2 {
  margin-bottom: 20px;
  color: #333;
}

.el-form {
  max-width: 600px;
}
</style>