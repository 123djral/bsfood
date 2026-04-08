<template>
  <div class="user page-shell">
    <div class="page-header">
      <div>
        <h2 class="page-title">个人中心</h2>
        <p class="page-subtitle">统一维护基础资料与饮食偏好，为后续食谱生成提供更精准的个性化依据。</p>
      </div>
    </div>

    <el-row :gutter="20" class="profile-grid">
      <el-col :xs="24" :lg="12">
        <el-card shadow="hover" class="profile-card">
          <template #header><div class="card-header"><span>基本信息</span></div></template>
          <el-form :model="userForm" label-width="100px">
            <el-form-item label="用户名">
              <el-input v-model="userForm.username" disabled></el-input>
            </el-form-item>
            <el-form-item label="年龄">
              <el-input v-model="userForm.age" type="number"></el-input>
            </el-form-item>
            <el-form-item label="性别">
              <el-select v-model="userForm.gender" placeholder="请选择性别">
                <el-option label="男" value="男"></el-option>
                <el-option label="女" value="女"></el-option>
              </el-select>
            </el-form-item>
            <el-form-item label="手机号码">
              <el-input v-model="userForm.phone"></el-input>
            </el-form-item>
            <el-form-item>
              <el-button type="primary" @click="updateUser">保存基本信息</el-button>
            </el-form-item>
          </el-form>
        </el-card>
      </el-col>
      <el-col :xs="24" :lg="12">
        <el-card shadow="hover" class="profile-card">
          <template #header><div class="card-header"><span>饮食偏好</span></div></template>
          <el-form :model="preferenceForm" label-width="100px">
            <el-form-item label="健康目标">
              <el-select v-model="preferenceForm.healthGoal" placeholder="请选择">
                <el-option label="减脂" value="减脂"></el-option>
                <el-option label="增肌" value="增肌"></el-option>
                <el-option label="控糖" value="控糖"></el-option>
                <el-option label="保持健康" value="保持健康"></el-option>
              </el-select>
            </el-form-item>
            <el-form-item label="饮食限制">
              <el-input v-model="preferenceForm.dietLimit" placeholder="如：素食、对花生过敏"></el-input>
            </el-form-item>
            <el-form-item label="口味倾向">
              <el-select v-model="preferenceForm.tastePreference" placeholder="请选择">
                <el-option label="清淡" value="清淡"></el-option>
                <el-option label="偏辣" value="偏辣"></el-option>
                <el-option label="偏甜" value="偏甜"></el-option>
                <el-option label="偏咸" value="偏咸"></el-option>
                <el-option label="偏酸" value="偏酸"></el-option>
              </el-select>
            </el-form-item>
            <el-form-item label="烹饪熟练度">
              <el-select v-model="preferenceForm.cookingLevel" placeholder="请选择">
                <el-option label="新手" value="新手"></el-option>
                <el-option label="进阶" value="进阶"></el-option>
                <el-option label="专业" value="专业"></el-option>
              </el-select>
            </el-form-item>
            <el-form-item>
              <el-button type="primary" @click="updatePreference">保存偏好设置</el-button>
            </el-form-item>
          </el-form>
        </el-card>
      </el-col>
    </el-row>
  </div>
</template>

<script>
import { userApi } from '../api/index.js'

export default {
  name: 'User',
  data() {
    return {
      userForm: {
        id: null,
        username: '',
        age: '',
        gender: '',
        phone: ''
      },
      preferenceForm: {
        userId: null,
        healthGoal: '',
        dietLimit: '',
        tastePreference: '',
        cookingLevel: ''
      }
    }
  },
  mounted() {
    const user = JSON.parse(localStorage.getItem('user') || 'null')
    if (user) {
      this.userForm = { ...user, age: user.age || '' }
      this.preferenceForm.userId = user.id
      this.loadPreference(user.id)
    }
  },
  methods: {
    async loadPreference(userId) {
      try {
        const res = await userApi.getPreference(userId)
        if (res.code === 200 && res.data) {
          this.preferenceForm = { ...res.data, userId }
        }
      } catch (e) {
        console.error('加载偏好失败:', e)
      }
    },
    async updateUser() {
      try {
        const res = await userApi.update({
          id: this.userForm.id,
          age: this.userForm.age ? parseInt(this.userForm.age) : null,
          gender: this.userForm.gender,
          phone: this.userForm.phone
        })
        if (res.code === 200) {
          this.$message.success('保存成功')
          // 更新本地存储
          const user = JSON.parse(localStorage.getItem('user') || '{}')
          Object.assign(user, { age: this.userForm.age, gender: this.userForm.gender, phone: this.userForm.phone })
          localStorage.setItem('user', JSON.stringify(user))
        } else {
          this.$message.error(res.message)
        }
      } catch (e) {
        this.$message.error('保存失败')
      }
    },
    async updatePreference() {
      try {
        const res = await userApi.updatePreference(this.preferenceForm)
        if (res.code === 200) {
          this.$message.success('偏好保存成功')
        } else {
          this.$message.error(res.message)
        }
      } catch (e) {
        this.$message.error('保存失败')
      }
    }
  }
}
</script>

<style scoped>
.user {
  display: flex;
  flex-direction: column;
  gap: 22px;
}

.page-header {
  display: flex;
  flex-direction: column;
  gap: 4px;
}

.profile-grid {
  row-gap: 20px;
}

.profile-card {
  height: 100%;
}

.profile-card :deep(.el-card__header) {
  padding: 20px 24px 0;
  border-bottom: none;
}

.profile-card :deep(.el-card__body) {
  padding: 18px 24px 24px;
}

.card-header {
  display: flex;
  justify-content: space-between;
  font-size: 18px;
  font-weight: 700;
  color: var(--text-primary);
}

@media (max-width: 768px) {
  .profile-card :deep(.el-card__header) {
    padding: 18px 18px 0;
  }

  .profile-card :deep(.el-card__body) {
    padding: 16px 18px 18px;
  }
}
</style>
