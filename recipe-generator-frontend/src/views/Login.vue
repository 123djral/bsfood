<template>
  <div class="login-container">
    <div class="login-background"></div>
    <el-card class="login-card" shadow="hover">
      <template #header>
        <div class="card-header">
          <span class="auth-badge">AI 饮食助手</span>
          <h2>{{ isRegister ? '创建你的饮食档案' : '欢迎回来' }}</h2>
          <p>{{ isRegister ? '填写基础信息后即可开始生成个性化食谱。' : '登录后继续管理食材、生成食谱与查看营养分析。' }}</p>
        </div>
      </template>
      <el-form :model="form" :rules="rules" ref="formRef" label-width="80px" class="auth-form">
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
          <el-button type="primary" @click="handleSubmit" :loading="loading" class="submit-button">
            {{ isRegister ? '注册' : '登录' }}
          </el-button>
        </el-form-item>
        <el-form-item class="switch-row">
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
  position: relative;
  display: flex;
  justify-content: center;
  align-items: center;
  min-height: calc(100vh - 190px);
  padding: 24px 0;
}

.login-background {
  position: absolute;
  inset: 0;
  border-radius: 28px;
  background:
    radial-gradient(circle at top left, rgba(37, 99, 235, 0.14), transparent 30%),
    radial-gradient(circle at bottom right, rgba(79, 70, 229, 0.12), transparent 26%),
    rgba(255, 255, 255, 0.45);
  filter: blur(0.2px);
}

.login-card {
  position: relative;
  z-index: 1;
  width: min(460px, 100%);
  border: 1px solid rgba(255, 255, 255, 0.82);
  background: rgba(255, 255, 255, 0.9);
  backdrop-filter: blur(12px);
}

.login-card :deep(.el-card__header) {
  padding: 28px 28px 12px;
  border-bottom: none;
}

.login-card :deep(.el-card__body) {
  padding: 8px 28px 28px;
}

.card-header {
  text-align: center;
}

.auth-badge {
  display: inline-flex;
  padding: 6px 12px;
  border-radius: 999px;
  background: rgba(37, 99, 235, 0.1);
  color: #2563eb;
  font-size: 12px;
  font-weight: 700;
  letter-spacing: 0.08em;
}

.card-header h2 {
  margin: 14px 0 8px;
  font-size: 28px;
  line-height: 1.2;
  color: var(--text-primary);
}

.card-header p {
  margin: 0;
  color: var(--text-secondary);
  line-height: 1.7;
}

.auth-form :deep(.el-form-item) {
  margin-bottom: 22px;
}

.auth-form :deep(.el-form-item__label) {
  color: var(--text-secondary);
  font-weight: 600;
}

.submit-button {
  width: 100%;
  height: 44px;
  border-radius: 12px;
  font-weight: 600;
}

.switch-row :deep(.el-form-item__content) {
  justify-content: center;
}

@media (max-width: 768px) {
  .login-container {
    min-height: auto;
    padding: 8px 0;
  }

  .login-background {
    border-radius: 20px;
  }

  .login-card :deep(.el-card__header) {
    padding: 24px 20px 10px;
  }

  .login-card :deep(.el-card__body) {
    padding: 8px 20px 22px;
  }

  .card-header h2 {
    font-size: 24px;
  }
}
</style>
