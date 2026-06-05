<template>
  <div class="min-h-screen w-full flex items-center justify-center login-bg">
    <!-- 艺术背景装饰 - 保持不变 -->
    <div class="absolute inset-0 overflow-hidden">
      <!-- 渐变层 -->
      <div class="absolute inset-0 bg-gradient-to-br from-slate-900 via-emerald-950/30 to-slate-900"></div>
      
      <!-- 动态光晕 -->
      <div class="glow-orb glow-1"></div>
      <div class="glow-orb glow-2"></div>
      <div class="glow-orb glow-3"></div>
      
      <!-- 几何网格 -->
      <div class="grid-pattern"></div>
      
      <!-- 流动曲线 -->
      <svg class="absolute inset-0 w-full h-full opacity-25" viewBox="0 0 1200 800" preserveAspectRatio="xMidYMid slice">
        <defs>
          <linearGradient id="curveGrad1" x1="0%" y1="0%" x2="100%" y2="100%">
            <stop offset="0%" style="stop-color:#10b981;stop-opacity:0.3" />
            <stop offset="100%" style="stop-color:#06b6d4;stop-opacity:0" />
          </linearGradient>
          <linearGradient id="curveGrad2" x1="0%" y1="0%" x2="100%" y2="0%">
            <stop offset="0%" style="stop-color:#0ea5e9;stop-opacity:0" />
            <stop offset="50%" style="stop-color:#14b8a6;stop-opacity:0.4" />
            <stop offset="100%" style="stop-color:#3b82f6;stop-opacity:0" />
          </linearGradient>
        </defs>
        <path class="floating-curve curve-1" d="M0,400 Q300,200 600,400 T1200,400" fill="none" stroke="url(#curveGrad1)" stroke-width="3" />
        <path class="floating-curve curve-2" d="M0,500 Q400,300 800,500 T1200,500" fill="none" stroke="url(#curveGrad2)" stroke-width="2" />
        <path class="floating-curve curve-3" d="M0,600 Q500,450 1000,600 T1200,600" fill="none" stroke="url(#curveGrad1)" stroke-width="1.5" />
      </svg>
      
      <!-- 粒子效果 -->
      <div class="particles-container">
        <div v-for="i in 20" :key="i" class="particle" :style="{ 
          left: `${Math.random() * 100}%`, 
          top: `${Math.random() * 100}%`,
          animationDelay: `${Math.random() * 5}s`,
          animationDuration: `${10 + Math.random() * 15}s`,
          width: `${2 + Math.random() * 4}px`,
          height: `${2 + Math.random() * 4}px`
        }"></div>
      </div>
      
      <!-- 角落装饰 -->
      <div class="corner-decor top-left"></div>
      <div class="corner-decor top-right"></div>
      <div class="corner-decor bottom-left"></div>
      <div class="corner-decor bottom-right"></div>
    </div>

    <!-- 登录卡片 - 重新设计 -->
    <div class="login-card-wrapper relative z-10 w-full max-w-md mx-4">
      <!-- 卡片光晕效果 -->
      <div class="card-glow"></div>
      
      <!-- 主卡片 -->
      <div class="login-card relative bg-white/90 backdrop-blur-2xl rounded-[24px] shadow-2xl overflow-hidden border border-white/40">
        <!-- 登录表单 -->
        <div class="form-content">
          <!-- 品牌Logo区域 -->
          <div class="brand-area">
            <h2 class="brand-title">小石头在线</h2>
            <p class="brand-subtitle">V1.0.0</p>
          </div>

          <el-form ref="loginFormRef" :model="loginForm" :rules="loginRules" class="form-body">
            <!-- 用户名 -->
            <div class="input-wrapper">
              <el-form-item prop="username" class="input-item">
                <el-input
                  v-model="loginForm.username"
                  placeholder="请输入用户名"
                  prefix-icon="User"
                  clearable
                  class="custom-input"
                />
              </el-form-item>
            </div>

            <!-- 密码 -->
            <div class="input-wrapper">
              <el-form-item prop="password" class="input-item">
                <el-input
                  v-model="loginForm.password"
                  type="password"
                  placeholder="请输入密码"
                  prefix-icon="Lock"
                  clearable
                  show-password
                  class="custom-input"
                />
              </el-form-item>
            </div>

            <!-- 记住我 & 忘记密码 -->
            <div class="form-options">
              <label class="remember-check">
                <el-checkbox v-model="loginForm.remember" />
                <span>记住我</span>
              </label>
            </div>

            <!-- 登录按钮 -->
            <el-form-item class="submit-item">
              <el-button type="primary" class="submit-btn" :loading="isLoading" @click="handleLogin">
                <el-icon :size="18" class="btn-icon">
                  <component :is="'LogIn'" />
                </el-icon>
                <span>登 录</span>
              </el-button>
            </el-form-item>

          </el-form>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, onMounted, onUnmounted } from 'vue'
import { useRouter } from 'vue-router'
import { useUserStore } from '@/stores/user'
import { ElMessage, ElForm } from 'element-plus'

const router = useRouter()
const userStore = useUserStore()

const isLoading = ref(false)

// 表单引用
const loginFormRef = ref<InstanceType<typeof ElForm> | null>(null)

// 登录表单
const loginForm = reactive({
  username: '',
  password: '',
  remember: false
})

// 登录表单验证规则
const loginRules = reactive({
  username: [
    { required: true, message: '请输入账号', trigger: 'blur' },
    { min: 3, max: 50, message: '账号长度在3-50个字符之间', trigger: 'blur' }
  ],
  password: [
    { required: true, message: '请输入密码', trigger: 'blur' },
    { min: 6, max: 32, message: '密码长度在6-32个字符之间', trigger: 'blur' }
  ]
})

// 登录处理
const handleLogin = async () => {
  try {
    if (loginFormRef.value) {
      //await loginFormRef.value.validate()
    }
  } catch {
    return
  }

  isLoading.value = true

  try {
    await userStore.login(loginForm)

    // Save username if "Remember Me" is checked
    if (loginForm.remember) {
      localStorage.setItem('remembered_username', loginForm.username)
      localStorage.setItem('is_remembered', 'true')
    } else {
      // Clear saved username if "Remember Me" is unchecked
      localStorage.removeItem('remembered_username')
      localStorage.removeItem('is_remembered')
    }

    ElMessage.success('登录成功！')
    router.push('/')
  } catch (error) {
    ElMessage.error('登录失败：账号或密码错误')
    console.error('登录错误：', error)
  } finally {
    isLoading.value = false
  }
}

// Handle Enter key press
const handleKeydown = (event: KeyboardEvent) => {
  if (event.key === 'Enter') {
    handleLogin()
  }
}

// Add and remove event listener
onMounted(() => {
  const savedUsername = localStorage.getItem('remembered_username')
  const isRemembered = localStorage.getItem('is_remembered') === 'true'
  
  if (savedUsername && isRemembered) {
    loginForm.username = savedUsername
    loginForm.remember = true
    // 注意：这里不填充密码，用户需要手动输入密码，这是安全的最佳实践
  }

  window.addEventListener('keydown', handleKeydown)
})

onUnmounted(() => {
  window.removeEventListener('keydown', handleKeydown)
})

</script>

<style scoped>
/* 登录卡片包装器 */
.login-card-wrapper {
  position: relative;
}

.card-glow {
  position: absolute;
  inset: -4px;
  background: linear-gradient(135deg, rgba(16, 185, 129, 0.15), rgba(6, 182, 212, 0.15));
  border-radius: 28px;
  filter: blur(20px);
  z-index: -1;
  animation: glowPulse 6s ease-in-out infinite;
}

@keyframes glowPulse {
  0%, 100% {
    opacity: 0.5;
    transform: scale(1);
  }
  50% {
    opacity: 0.8;
    transform: scale(1.01);
  }
}

/* 登录卡片 */
.login-card {
  position: relative;
  background: rgba(15, 23, 42, 0.85);
  backdrop-filter: blur(20px);
  border-radius: 24px;
  box-shadow: 0 25px 60px rgba(0, 0, 0, 0.4),
              0 0 0 1px rgba(255, 255, 255, 0.05) inset;
  border: 1px solid rgba(255, 255, 255, 0.1);
  overflow: hidden;
}

@keyframes gradientFlow {
  0% {
    background-position: 0% 50%;
  }
  100% {
    background-position: 200% 50%;
  }
}

/* 表单内容 */
.form-content {
  padding: 0 32px 32px;
}

/* 品牌区域 */
.brand-area {
  text-align: center;
  margin-top: 30px;
  margin-bottom: 10px;
}

.logo-circle {
  width: 72px;
  height: 72px;
  margin: 0 auto 16px;
  background: linear-gradient(135deg, #10b981, #14b8a6, #06b6d4);
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
  box-shadow: 0 8px 24px rgba(16, 185, 129, 0.3);
  position: relative;
}

.logo-circle::before {
  content: '';
  position: absolute;
  inset: 3px;
  background: rgba(255, 255, 255, 0.1);
  border-radius: 50%;
}

.logo-icon {
  color: #fff;
  position: relative;
  z-index: 1;
}

.brand-title {
  font-size: 28px;
  font-weight: 700;
  margin: 0 0 6px;
  background: linear-gradient(90deg, #10b981, #14b8a6, #06b6d4);
  -webkit-background-clip: text;
  -webkit-text-fill-color: transparent;
  background-clip: text;
}

.brand-subtitle {
  font-size: 14px;
  color: #9ca3af;
  margin: 0;
}

/* 表单主体 */
.form-body {
  display: flex;
  flex-direction: column;
  gap: 16px;
}

/* 输入框包装器 */
.input-wrapper {
  position: relative;
}

.input-wrapper::before {
  content: '';
  position: absolute;
  inset: -2px;
  background: linear-gradient(135deg, rgba(16, 185, 129, 0.2), rgba(6, 182, 212, 0.2));
  border-radius: 16px;
  opacity: 0;
  transition: opacity 0.3s ease;
  z-index: -1;
}

.input-wrapper:focus-within::before {
  opacity: 1;
}

/* 表单项 */
.input-item {
  margin-bottom: 0;
}

/* 自定义输入框 */
.custom-input {
  height: 52px !important;
  border-radius: 14px !important;
  border: none !important;
  background: transparent !important;
  color: #f3f4f6 !important;
  font-size: 14px !important;
  transition: all 0.3s cubic-bezier(0.4, 0, 0.2, 1) !important;
  padding: 0 !important;
}

/* 穿透到输入框外层容器 */
.custom-input :deep(.el-input) {
  height: 100% !important;
  background: rgba(255, 255, 255, 0.03) !important;
  border: 1px solid rgba(255, 255, 255, 0.15) !important;
  border-radius: 14px !important;
  transition: all 0.3s ease !important;
}

.custom-input :deep(.el-input):hover {
  background: rgba(255, 255, 255, 0.05) !important;
  border-color: rgba(255, 255, 255, 0.25) !important;
}

.custom-input :deep(.el-input__wrapper) {
  height: 100% !important;
  background: transparent !important;
  border: none !important;
  border-radius: 14px !important;
  box-shadow: none !important;
}

.custom-input :deep(.el-input__inner) {
  height: 100% !important;
  background: transparent !important;
  color: #f3f4f6 !important;
  border: none !important;
  border-radius: 14px !important;
  box-shadow: none !important;
  padding: 0 16px !important;
}

.custom-input :deep(.el-input__inner)::placeholder {
  color: #6b7280 !important;
}

/* 聚焦状态 */
.custom-input:focus-within :deep(.el-input) {
  border-color: rgba(16, 185, 129, 0.6) !important;
  box-shadow: 0 0 0 4px rgba(16, 185, 129, 0.15) !important;
  background: rgba(255, 255, 255, 0.05) !important;
}

/* 输入框图标颜色 */
.custom-input :deep(.el-input__prefix),
.custom-input :deep(.el-input__suffix) {
  color: #6b7280 !important;
}

.custom-input:focus-within :deep(.el-input__prefix),
.custom-input:focus-within :deep(.el-input__suffix) {
  color: #10b981 !important;
}

/* 清除按钮样式 */
.custom-input :deep(.el-input__clear) {
  color: #6b7280 !important;
  background: rgba(255, 255, 255, 0.1) !important;
  border-radius: 50% !important;
}

.custom-input :deep(.el-input__clear):hover {
  color: #f3f4f6 !important;
  background: rgba(255, 255, 255, 0.2) !important;
}

/* 表单选项 */
.form-options {
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: 0 4px;
}

.remember-check {
  display: flex;
  align-items: center;
  gap: 8px;
  cursor: pointer;
  font-size: 14px;
  color: #d1d5db;
}

/* 复选框样式 */
.remember-check :deep(.el-checkbox__input) {
  border-color: rgba(255, 255, 255, 0.25) !important;
  background: rgba(255, 255, 255, 0.05) !important;
  width: 18px !important;
  height: 18px !important;
  border-radius: 4px !important;
  display: flex !important;
  align-items: center !important;
  justify-content: center !important;
}

.remember-check :deep(.el-checkbox__input):hover {
  border-color: rgba(16, 185, 129, 0.6) !important;
  background: rgba(16, 185, 129, 0.1) !important;
}

.remember-check :deep(.el-checkbox__input.is-checked) {
  background: linear-gradient(135deg, #10b981, #06b6d4) !important;
  border-color: transparent !important;
}

.remember-check :deep(.el-checkbox__input.is-focus) {
  box-shadow: 0 0 0 3px rgba(16, 185, 129, 0.2) !important;
}

/* 复选框勾选图标 */
.remember-check :deep(.el-checkbox__inner) {
  width: 18px !important;
  height: 18px !important;
  border: none !important;
  background: transparent !important;
  display: flex !important;
  align-items: center !important;
  justify-content: center !important;
}

.remember-check :deep(.el-checkbox__inner::after) {
  width: 5px !important;
  height: 9px !important;
  border-width: 0 2px 2px 0 !important;
  border-color: #fff !important;
  left: 50% !important;
  top: 50% !important;
  transform: translate(-50%, -55%) rotate(45deg) !important;
  margin: 0 !important;
}

/* 提交按钮 */
.submit-item {
  margin-bottom: 0;
  margin-top: 8px;
}

.submit-btn {
  width: 100%;
  height: 45px !important;
  border-radius: 16px !important;
  font-weight: 600 !important;
  font-size: 16px !important;
  border: none !important;
  transition: all 0.3s cubic-bezier(0.4, 0, 0.2, 1) !important;
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 10px;
  background: linear-gradient(135deg, #10b981, #14b8a6, #06b6d4) !important;
  box-shadow: 0 6px 20px rgba(16, 185, 129, 0.35) !important;
}

.submit-btn:hover {
  background: linear-gradient(135deg, #059669, #0d9488, #0891b2) !important;
  box-shadow: 0 8px 24px rgba(16, 185, 129, 0.45) !important;
  transform: translateY(-2px);
}

.submit-btn:active {
  transform: translateY(0);
}

.btn-icon {
  transition: transform 0.3s ease;
}

.submit-btn:hover .btn-icon {
  transform: translateX(4px);
}

/* 提示文字 */
.signup-hint,
.login-hint {
  text-align: center;
  font-size: 14px;
  color: #9ca3af;
  margin: 20px 0 0;
}

.signup-link,
.login-link {
  color: #10b981;
  font-weight: 600;
  text-decoration: none;
  transition: all 0.3s ease;
  margin-left: 4px;
}

.signup-link:hover,
.login-link:hover {
  color: #059669;
  text-decoration: underline;
}

/* 响应式设计 */
@media (max-width: 480px) {
  .login-card-wrapper {
    margin: 0 16px;
  }

  .login-card {
    border-radius: 20px;
  }

  .tabs-container {
    padding: 24px 20px 12px;
  }

  .tabs-bg {
    left: 20px;
    right: 20px;
  }

  .tab-indicator {
    left: 20px;
    width: calc(50% - 20px);
  }

  .form-content {
    padding: 0 20px 24px;
  }

  .brand-area {
    margin-bottom: 24px;
  }

  .logo-circle {
    width: 60px;
    height: 60px;
    margin-bottom: 12px;
  }

  .brand-title {
    font-size: 22px;
  }

  .custom-input {
    height: 48px !important;
  }

  .submit-btn {
    height: 52px !important;
  }
}
</style>
