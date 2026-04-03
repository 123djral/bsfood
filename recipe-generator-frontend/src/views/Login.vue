<template>
  <div class="login-container">
    <el-card class="login-card" shadow="hover">
      <template #header>
        <div class="card-header">
          <h2>{{ isRegister ? '用户注册' : '用户登录' }}</h2>
        </div>
      </template>
      <el-form :model="form" :rules="rules" ref="formRef" label-width="80px">
        <el-form-item label="用户名" prop="username">
          <el-input v-model="form.username" placeholder="请输入用户名"></el-input>
        </el-form-item>
        <el-form-item label="密码" prop="password">
          <el-input v-model="form.password" type="password" placeholder="请输入密码" show-password></el-input>
        </el-form-item>
        <template v-if="isRegister">
          <el-form-item label="年龄">
            <el-input v-model="form.age" placeholder="请输入年龄" type="number"></el-input>
          </el-form-item>
          <el-form-item label="性别">
            <el-select v-model="form.gender" placeholder="请选择性别">
              <el-option label="男" value="男"></el-option>
              <el-option label="女" value="女"></el-option>
            </el-select>
          </el-form-item>
          <el-form-item label="手机号码">
            <el-input v-model="form.phone" placeholder="请输入手机号码"></el-input>
          </el-form-item>
        </template>
        <el-form-item>
          <el-button type="primary" @click="handleSubmit" :loading="loading" style="width: 100%;">
            {{ isRegister ? '注册' : '登录' }}
          </el-button>
        </el-form-item>
        <el-form-item>
          <el-link type="primary" @click="isRegister = !isRegister">
            {{ isRegister ? '已有账号？去登录' : '没有账号？去注册' }}
          </el-link>
        </el-form-item>
      </el-form>
    </el-card>
  </div>
</template>

<script>
import { userApi } from '../api/index.js'

export default {
  name: 'Login',
  data() {
    return {
      isRegister: false,
      loading: false,
      form: {
        username: '',
        password: '',
        age: '',
        gender: '',
        phone: ''
      },
      rules: {
        username: [{ required: true, message: '请输入用户名', trigger: 'blur' }],
        password: [{ required: true, message: '请输入密码', trigger: 'blur' }]
      }
    }
  },
  methods: {
    async handleSubmit() {
      try {
        await this.$refs.formRef.validate()
      } catch {
        return
      }

      this.loading = true
      try {
        if (this.isRegister) {
          const res = await userApi.register({
            username: this.form.username,
            password: this.form.password,
            age: this.form.age ? parseInt(this.form.age) : null,
            gender: this.form.gender,
            phone: this.form.phone
          })
          if (res.code === 200) {
            this.$message.success('注册成功，请登录')
            this.isRegister = false
          } else {
            this.$message.error(res.message)
          }
        } else {
          const res = await userApi.login(this.form.username, this.form.password)
          if (res.code === 200) {
            localStorage.setItem('user', JSON.stringify(res.data))
            this.$message.success('登录成功')
            this.$router.push('/')
          } else {
            this.$message.error(res.message)
          }
        }
      } catch (e) {
        this.$message.error('操作失败，请重试')
      } finally {
        this.loading = false
      }
    }
  }
}
</script>

<style scoped>
.login-container {
  display: flex;
  justify-content: center;
  align-items: center;
  min-height: 70vh;
}

.login-card {
  width: 420px;
}

.card-header {
  text-align: center;
}

.card-header h2 {
  margin: 0;
  color: #333;
}
</style>
