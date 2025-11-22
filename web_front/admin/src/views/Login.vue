<template>
  <div class="login-container">
    <el-card class="login-card" :body-style="{ padding: '0' }">
      <div class="title">
        <h1>
          <div class="title-icon">❄️</div>
          云阶雪阁管理后台
        </h1>
        <p>{{ isRegister ? '创建你的店铺账号' : '欢迎回来，请登录' }}</p>
      </div>
      
      <!-- 登录表单 -->
      <transition name="fade" mode="out-in">
        <el-form v-if="!isRegister" :model="loginForm" :rules="loginRules" ref="loginFormRef" key="login">
          <el-form-item prop="username">
            <el-input
              v-model="loginForm.username"
              placeholder="请输入账号"
              prefix-icon="User"
              size="large"
            />
          </el-form-item>
          <el-form-item prop="password">
            <el-input
              v-model="loginForm.password"
              type="password"
              placeholder="请输入密码"
              prefix-icon="Lock"
              size="large"
              show-password
            />
          </el-form-item>
          <el-form-item>
            <el-button
              type="primary"
              size="large"
              @click="handleLogin"
              :loading="loading"
            >
              登录
            </el-button>
          </el-form-item>
          <div class="form-footer">
            <el-link type="primary" @click="isRegister = true" :underline="false">
              还没有账号？点击注册
            </el-link>
          </div>
        </el-form>
        
        <!-- 注册表单 -->
        <el-form v-else :model="registerForm" :rules="registerRules" ref="registerFormRef" key="register" class="register-form">
          <!-- 店铺信息 -->
          <div class="form-section">
            <div class="section-title">
              <el-icon><Shop /></el-icon>
              <span>店铺信息</span>
            </div>
            <el-form-item prop="tenantName">
              <el-input
                v-model="registerForm.tenantName"
                placeholder="请输入店铺名称"
                prefix-icon="Shop"
                size="large"
              />
            </el-form-item>
          </div>

          <!-- 个人信息 -->
          <div class="form-section">
            <div class="section-title">
              <el-icon><User /></el-icon>
              <span>个人信息</span>
            </div>
            <el-row :gutter="16">
              <el-col :span="12">
                <el-form-item prop="name">
                  <el-input
                    v-model="registerForm.name"
                    placeholder="请输入您的姓名"
                    prefix-icon="User"
                    size="large"
                  />
                </el-form-item>
              </el-col>
              <el-col :span="12">
                <el-form-item prop="mobile">
                  <el-input
                    v-model="registerForm.mobile"
                    placeholder="请输入手机号"
                    prefix-icon="Phone"
                    size="large"
                  />
                </el-form-item>
              </el-col>
            </el-row>
          </div>

          <!-- 账号信息 -->
          <div class="form-section">
            <div class="section-title">
              <el-icon><Key /></el-icon>
              <span>账号信息</span>
            </div>
            <el-form-item prop="username">
              <el-input
                v-model="registerForm.username"
                placeholder="请设置登录账号"
                prefix-icon="User"
                size="large"
              />
            </el-form-item>
            <el-row :gutter="16">
              <el-col :span="12">
                <el-form-item prop="password">
                  <el-input
                    v-model="registerForm.password"
                    type="password"
                    placeholder="请设置登录密码"
                    prefix-icon="Lock"
                    size="large"
                    show-password
                  />
                </el-form-item>
              </el-col>
              <el-col :span="12">
                <el-form-item prop="confirmPassword">
                  <el-input
                    v-model="registerForm.confirmPassword"
                    type="password"
                    placeholder="请再次输入密码"
                    prefix-icon="Lock"
                    size="large"
                    show-password
                  />
                </el-form-item>
              </el-col>
            </el-row>
          </div>

          <el-form-item>
            <el-button
              type="primary"
              size="large"
              @click="handleRegister"
              :loading="loading"
            >
              注册
            </el-button>
          </el-form-item>
          <div class="form-footer">
            <el-link type="primary" @click="isRegister = false" :underline="false">
              已有账号？点击登录
            </el-link>
          </div>
        </el-form>
      </transition>
    </el-card>
  </div>
</template>

<script setup>
import { ref } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import { Shop, User, Key } from '@element-plus/icons-vue'
import request from '../utils/request'

const router = useRouter()
const loginFormRef = ref(null)
const registerFormRef = ref(null)
const loading = ref(false)
const isRegister = ref(false)

// 登录表单
const loginForm = ref({
  username: '',
  password: ''
})

// 注册表单
const registerForm = ref({
  tenantName: '',
  name: '',
  mobile: '',
  username: '',
  password: '',
  confirmPassword: ''
})

// 登录验证规则
const loginRules = {
  username: [
    { required: true, message: '请输入账号', trigger: 'blur' }
  ],
  password: [
    { required: true, message: '请输入密码', trigger: 'blur' }
  ]
}

// 注册验证规则
const registerRules = {
  tenantName: [
    { required: true, message: '请输入店铺名称', trigger: 'blur' }
  ],
  name: [
    { required: true, message: '请输入姓名', trigger: 'blur' }
  ],
  mobile: [
    { required: true, message: '请输入手机号', trigger: 'blur' },
    { pattern: /^1[3-9]\d{9}$/, message: '手机号格式不正确', trigger: 'blur' }
  ],
  username: [
    { required: true, message: '请输入登录账号', trigger: 'blur' },
    { min: 3, max: 20, message: '账号长度3-20个字符', trigger: 'blur' }
  ],
  password: [
    { required: true, message: '请输入密码', trigger: 'blur' },
    { min: 6, max: 20, message: '密码长度6-20个字符', trigger: 'blur' }
  ],
  confirmPassword: [
    { required: true, message: '请再次输入密码', trigger: 'blur' },
    {
      validator: (rule, value, callback) => {
        if (value !== registerForm.value.password) {
          callback(new Error('两次输入的密码不一致'))
        } else {
          callback()
        }
      },
      trigger: 'blur'
    }
  ]
}

// 登录
const handleLogin = async () => {
  await loginFormRef.value.validate()
  
  loading.value = true
  try {
    const res = await request.post('/auth/admin/login', {
      username: loginForm.value.username,
      password: loginForm.value.password
    })
    localStorage.setItem('token', res.data.token)
    localStorage.setItem('userInfo', JSON.stringify(res.data))
    ElMessage.success('登录成功')
    router.push('/')
  } catch (error) {
    console.error(error)
  } finally {
    loading.value = false
  }
}

// 注册
const handleRegister = async () => {
  await registerFormRef.value.validate()
  
  loading.value = true
  try {
    const res = await request.post('/auth/admin/register', registerForm.value)
    localStorage.setItem('token', res.data.token)
    localStorage.setItem('userInfo', JSON.stringify(res.data))
    ElMessage.success('注册成功！')
    router.push('/')
  } catch (error) {
    console.error(error)
  } finally {
    loading.value = false
  }
}
</script>

<style scoped>
.login-container {
  height: 100vh;
  display: flex;
  align-items: center;
  justify-content: center;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  position: relative;
  overflow: hidden;
}

/* 背景装饰 */
.login-container::before {
  content: '';
  position: absolute;
  width: 500px;
  height: 500px;
  background: rgba(255, 255, 255, 0.1);
  border-radius: 50%;
  top: -150px;
  right: -150px;
  animation: float 6s ease-in-out infinite;
}

.login-container::after {
  content: '';
  position: absolute;
  width: 400px;
  height: 400px;
  background: rgba(255, 255, 255, 0.05);
  border-radius: 50%;
  bottom: -100px;
  left: -100px;
  animation: float 8s ease-in-out infinite;
}

@keyframes float {
  0%, 100% {
    transform: translateY(0) rotate(0deg);
  }
  50% {
    transform: translateY(-20px) rotate(5deg);
  }
}

.login-card {
  width: 420px;
  padding: 40px;
  background: rgba(255, 255, 255, 0.95);
  backdrop-filter: blur(10px);
  border-radius: 20px;
  box-shadow: 0 20px 60px rgba(0, 0, 0, 0.3);
  position: relative;
  z-index: 1;
  animation: slideIn 0.5s ease-out;
  transition: all 0.3s;
}

/* 注册页面时卡片加宽 */
.login-card:has(.register-form) {
  width: 650px;
  max-height: 90vh;
  padding: 30px 40px;
  overflow-y: auto;
  /* 隐藏滚动条但保持滚动功能 */
  scrollbar-width: none; /* Firefox */
  -ms-overflow-style: none; /* IE/Edge */
}

/* 隐藏滚动条 - Webkit浏览器 */
.login-card:has(.register-form)::-webkit-scrollbar {
  display: none;
}

@keyframes slideIn {
  from {
    opacity: 0;
    transform: translateY(-30px);
  }
  to {
    opacity: 1;
    transform: translateY(0);
  }
}

.title {
  text-align: center;
  margin-bottom: 24px;
  position: relative;
}

.title h1 {
  font-size: 28px;
  font-weight: 600;
  color: #303133;
  margin: 0;
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 10px;
}

.title-icon {
  width: 40px;
  height: 40px;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  border-radius: 10px;
  display: flex;
  align-items: center;
  justify-content: center;
  color: white;
  font-size: 20px;
  animation: pulse 2s ease-in-out infinite;
}

@keyframes pulse {
  0%, 100% {
    transform: scale(1);
  }
  50% {
    transform: scale(1.05);
  }
}

.title p {
  color: #909399;
  font-size: 14px;
  margin: 8px 0 0 0;
}

/* 输入框样式 */
:deep(.el-input__wrapper) {
  border-radius: 10px;
  padding: 12px 15px;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.05);
  transition: all 0.3s;
}

:deep(.el-input__wrapper:hover) {
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.1);
}

:deep(.el-input__wrapper.is-focus) {
  box-shadow: 0 4px 16px rgba(102, 126, 234, 0.3);
}

:deep(.el-input__inner) {
  font-size: 15px;
}

:deep(.el-input__prefix) {
  font-size: 16px;
  color: #909399;
}

/* 按钮样式 */
:deep(.el-button--primary) {
  width: 100%;
  height: 48px;
  border-radius: 10px;
  font-size: 16px;
  font-weight: 500;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  border: none;
  box-shadow: 0 4px 15px rgba(102, 126, 234, 0.4);
  transition: all 0.3s;
}

:deep(.el-button--primary:hover) {
  transform: translateY(-2px);
  box-shadow: 0 6px 20px rgba(102, 126, 234, 0.6);
}

:deep(.el-button--primary:active) {
  transform: translateY(0);
}

/* 链接样式 */
:deep(.el-link) {
  font-size: 14px;
  font-weight: 500;
}

:deep(.el-form-item) {
  margin-bottom: 24px;
}

/* 注册表单样式 */
.register-form :deep(.el-form-item) {
  margin-bottom: 5px;
}

/* 表单分组 */
.form-section {
  margin-bottom: 8px;
}

.section-title {
  display: flex;
  align-items: center;
  gap: 6px;
  margin-bottom: 10px;
  padding-bottom: 4px;
  border-bottom: 2px solid #f0f0f0;
  color: #606266;
  font-size: 14px;
  font-weight: 600;
}

.section-title .el-icon {
  font-size: 18px;
  color: #667eea;
}

/* 注册表单分栏布局 */
.register-form :deep(.el-row) {
  margin-bottom: 0;
}

.register-form :deep(.el-col) {
  margin-bottom: 0;
}

.form-footer {
  text-align: center;
  margin-top: 12px;
}

/* 切换动画 */
.fade-enter-active,
.fade-leave-active {
  transition: opacity 0.3s, transform 0.3s;
}

.fade-enter-from {
  opacity: 0;
  transform: translateX(-10px);
}

.fade-leave-to {
  opacity: 0;
  transform: translateX(10px);
}
</style>
