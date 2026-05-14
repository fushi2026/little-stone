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
        <!-- 顶部装饰条 -->
        <div class="card-top-bar"></div>
        
        <!-- 标签切换区域 -->
        <div class="tabs-container">
          <div class="tabs-bg"></div>
          <div class="tab-indicator" :class="{ active: activeTab === 'register' }"></div>
          <button 
            v-for="tab in tabs" 
            :key="tab.key"
            class="tab-btn"
            :class="{ active: activeTab === tab.key }"
            @click="activeTab = tab.key"
          >
            <el-icon :size="18" class="tab-icon">
              <component :is="tab.key === 'login' ? 'LogIn' : 'UserPlus'" />
            </el-icon>
            <span class="tab-label">{{ tab.label }}</span>
          </button>
        </div>

        <!-- 登录表单 -->
        <div v-show="activeTab === 'login'" class="form-content">
          <!-- 品牌Logo区域 -->
          <div class="brand-area">
            <div class="logo-circle">
              <el-icon :size="32" class="logo-icon">
                <component :is="'ShieldCheck'" />
              </el-icon>
            </div>
            <h2 class="brand-title">Little Stone Online</h2>
            <p class="brand-subtitle">小石头在线</p>
          </div>

          <el-form ref="loginFormRef" :model="loginForm" :rules="loginRules" class="form-body">
            <!-- 用户名 -->
            <div class="input-wrapper">
              <el-form-item prop="username" class="input-item">
                <el-input
                  v-model="loginForm.username"
                  placeholder="手机号或邮箱"
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
              <a href="#" class="forgot-link">
                忘记密码？
                <el-icon :size="14" class="link-icon">
                  <component :is="'ArrowRight'" />
                </el-icon>
              </a>
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

          <!-- 社交登录 -->
          <div class="social-login">
            <div class="social-divider">
              <span>或使用以下方式登录</span>
            </div>
            <div class="social-btns">
              <button class="social-btn google-btn">
                <svg viewBox="0 0 24 24" class="social-icon">
                  <path fill="#4285F4" d="M22.56 12.25c0-.78-.07-1.53-.2-2.25H12v4.26h5.92c-.26 1.37-1.04 2.53-2.21 3.31v2.77h3.57c2.08-1.92 3.28-4.74 3.28-8.09z"/>
                  <path fill="#34A853" d="M12 23c2.97 0 5.46-.98 7.28-2.66l-3.57-2.77c-.98.66-2.23 1.06-3.71 1.06-2.86 0-5.29-1.93-6.16-4.53H2.18v2.84C3.99 20.53 7.7 23 12 23z"/>
                  <path fill="#FBBC05" d="M5.84 14.09c-.22-.66-.35-1.36-.35-2.09s.13-1.43.35-2.09V7.07H2.18C1.43 8.55 1 10.22 1 12s.43 3.45 1.18 4.93l2.85-2.22.81-.62z"/>
                  <path fill="#EA4335" d="M12 5.38c1.62 0 3.06.56 4.21 1.64l3.15-3.15C17.45 2.09 14.97 1 12 1 7.7 1 3.99 3.47 2.18 7.07l3.66 2.84c.87-2.6 3.3-4.53 6.16-4.53z"/>
                </svg>
                <span>Google</span>
              </button>
              <button class="social-btn wechat-btn">
                <svg viewBox="0 0 24 24" class="social-icon" fill="#07C160">
                  <path d="M8.691 2.188C3.891 2.188 0 5.476 0 9.53c0 2.212 1.17 4.203 3.002 5.55a.59.59 0 0 1 .213.665l-.39 1.48c-.019.07-.048.141-.048.213 0 .163.13.295.29.295a.326.326 0 0 0 .167-.054l1.903-1.114a.864.864 0 0 1 .717-.098 10.16 10.16 0 0 0 2.837.403c4.801 0 8.692-3.287 8.692-7.342 0-4.054-3.891-7.334-8.692-7.334z"/>
                </svg>
                <span>微信</span>
              </button>
            </div>
          </div>

          <!-- 注册提示 -->
          <p class="signup-hint">
            还没有账号？
            <a href="#" @click.prevent="activeTab = 'register'" class="signup-link">立即注册</a>
          </p>
        </div>

        <!-- 注册表单 -->
        <div v-show="activeTab === 'register'" class="form-content">
          <!-- 品牌Logo区域 -->
          <div class="brand-area">
            <div class="logo-circle register-logo">
              <el-icon :size="32" class="logo-icon">
                <component :is="'UserPlus'" />
              </el-icon>
            </div>
            <h2 class="brand-title">SmartStone</h2>
            <p class="brand-subtitle">创建您的账号</p>
          </div>

          <el-form ref="registerFormRef" :model="registerForm" :rules="registerRules" class="form-body">
            <!-- 用户名 -->
            <div class="input-wrapper">
              <el-form-item prop="username" class="input-item">
                <el-input
                  v-model="registerForm.username"
                  placeholder="请输入用户名"
                  prefix-icon="User"
                  clearable
                  class="custom-input"
                />
              </el-form-item>
            </div>

            <!-- 邮箱 -->
            <div class="input-wrapper">
              <el-form-item prop="email" class="input-item">
                <el-input
                  v-model="registerForm.email"
                  placeholder="请输入邮箱"
                  prefix-icon="Mail"
                  clearable
                  class="custom-input"
                />
              </el-form-item>
            </div>

            <!-- 手机号 -->
            <div class="input-wrapper">
              <el-form-item prop="phone" class="input-item">
                <el-input
                  v-model="registerForm.phone"
                  placeholder="请输入手机号"
                  prefix-icon="Phone"
                  clearable
                  class="custom-input"
                />
              </el-form-item>
            </div>

            <!-- 密码 -->
            <div class="input-wrapper">
              <el-form-item prop="password" class="input-item">
                <el-input
                  v-model="registerForm.password"
                  type="password"
                  placeholder="请输入密码"
                  prefix-icon="Lock"
                  clearable
                  show-password
                  class="custom-input"
                />
              </el-form-item>
            </div>

            <!-- 确认密码 -->
            <div class="input-wrapper">
              <el-form-item prop="confirmPassword" class="input-item">
                <el-input
                  v-model="registerForm.confirmPassword"
                  type="password"
                  placeholder="确认密码"
                  prefix-icon="Lock"
                  clearable
                  show-password
                  class="custom-input"
                />
              </el-form-item>
            </div>

            <!-- 同意条款 -->
            <el-form-item class="agreement-item">
              <label class="agreement-label">
                <el-checkbox v-model="registerForm.agreed" border />
                <span>我已阅读并同意</span>
                <a href="#" class="agreement-link">《服务条款》</a>
                <span>和</span>
                <a href="#" class="agreement-link">《隐私政策》</a>
              </label>
            </el-form-item>

            <!-- 注册按钮 -->
            <el-form-item class="submit-item">
              <el-button type="primary" class="submit-btn register-btn" :loading="isLoading" @click="handleRegister">
                <el-icon :size="18" class="btn-icon">
                  <component :is="'UserPlus'" />
                </el-icon>
                <span>注 册</span>
              </el-button>
            </el-form-item>
          </el-form>

          <!-- 登录提示 -->
          <p class="login-hint">
            已有账号？
            <a href="#" @click.prevent="activeTab = 'login'" class="login-link">立即登录</a>
          </p>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive } from 'vue'
import { useRouter } from 'vue-router'
import { useUserStore } from '@/stores/user'
import { ElMessage, ElForm } from 'element-plus'

const router = useRouter()
const userStore = useUserStore()

// 标签页配置
const tabs = [
  { key: 'login', label: '登录' },
  { key: 'register', label: '注册' }
]

const activeTab = ref('login')
const isLoading = ref(false)

// 表单引用
const loginFormRef = ref<InstanceType<typeof ElForm> | null>(null)
const registerFormRef = ref<InstanceType<typeof ElForm> | null>(null)

// 登录表单
const loginForm = reactive({
  username: '',
  password: '',
  remember: false
})

// 注册表单
const registerForm = reactive({
  username: '',
  email: '',
  phone: '',
  password: '',
  confirmPassword: '',
  agreed: false
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

// 注册表单验证规则
const registerRules = reactive({
  username: [
    { required: true, message: '请输入用户名', trigger: 'blur' },
    { min: 3, max: 50, message: '用户名长度在3-50个字符之间', trigger: 'blur' }
  ],
  email: [
    { required: true, message: '请输入邮箱', trigger: 'blur' },
    { type: 'email' as const, message: '请输入正确的邮箱格式', trigger: 'blur' }
  ],
  phone: [
    { required: true, message: '请输入手机号', trigger: 'blur' },
    { pattern: /^1[3-9]\d{9}$/, message: '请输入正确的手机号格式', trigger: 'blur' }
  ],
  password: [
    { required: true, message: '请输入密码', trigger: 'blur' },
    { min: 6, max: 32, message: '密码长度在6-32个字符之间', trigger: 'blur' },
    { pattern: /^(?=.*[a-zA-Z])(?=.*\d).+$/, message: '密码需要包含字母和数字', trigger: 'blur' }
  ],
  confirmPassword: [
    { required: true, message: '请确认密码', trigger: 'blur' },
    {
      validator: (_rule: any, value: string, callback: any) => {
        if (value !== registerForm.password) {
          callback(new Error('两次输入的密码不一致'))
        } else {
          callback()
        }
      },
      trigger: 'blur'
    }
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
    ElMessage.success('登录成功！')
    router.push('/')
  } catch (error) {
    ElMessage.error('登录失败：账号或密码错误')
    console.error('登录错误：', error)
  } finally {
    isLoading.value = false
  }
}

// 注册处理
const handleRegister = async () => {
  if (!registerForm.agreed) {
    ElMessage.warning('请先阅读并同意服务条款和隐私政策')
    return
  }

  try {
    if (registerFormRef.value) {
      await registerFormRef.value.validate()
    }
  } catch {
    return
  }

  isLoading.value = true

  try {
    await new Promise(resolve => setTimeout(resolve, 1000))
    ElMessage.success('注册成功！')
    activeTab.value = 'login'
    if (registerFormRef.value) {
      registerFormRef.value.resetFields()
    }
    registerForm.agreed = false
  } catch (error) {
    ElMessage.error('注册失败，请稍后重试')
    console.error('注册错误：', error)
  } finally {
    isLoading.value = false
  }
}
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

/* 顶部装饰条 */
.card-top-bar {
  position: absolute;
  top: 0;
  left: 0;
  right: 0;
  height: 3px;
  background: linear-gradient(90deg, #10b981, #14b8a6, #06b6d4, #0ea5e9);
  background-size: 200% 100%;
  animation: gradientFlow 4s linear infinite;
}

@keyframes gradientFlow {
  0% {
    background-position: 0% 50%;
  }
  100% {
    background-position: 200% 50%;
  }
}

/* 标签切换区域 */
.tabs-container {
  position: relative;
  display: flex;
  padding: 32px 32px 16px;
}

.tabs-bg {
  position: absolute;
  bottom: 16px;
  left: 32px;
  right: 32px;
  height: 6px;
  background: rgba(255, 255, 255, 0.1);
  border-radius: 3px;
}

.tab-indicator {
  position: absolute;
  bottom: 16px;
  left: 32px;
  width: calc(50% - 32px);
  height: 6px;
  background: linear-gradient(90deg, #10b981, #14b8a6, #06b6d4);
  border-radius: 3px;
  transition: transform 0.5s cubic-bezier(0.4, 0, 0.2, 1);
  box-shadow: 0 2px 8px rgba(16, 185, 129, 0.35);
}

.tab-indicator.active {
  transform: translateX(100%);
}

.tab-btn {
  flex: 1;
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 8px;
  padding: 12px 20px;
  background: transparent;
  border: none;
  cursor: pointer;
  font-weight: 600;
  font-size: 16px;
  color: #9ca3af;
  transition: all 0.3s ease;
  border-radius: 12px;
  position: relative;
  z-index: 1;
}

.tab-btn:hover {
  color: #e5e7eb;
  background: rgba(16, 185, 129, 0.1);
}

.tab-btn.active {
  color: #10b981;
}

.tab-icon {
  transition: transform 0.3s ease;
}

.tab-btn.active .tab-icon {
  transform: scale(1.1);
}

/* 表单内容 */
.form-content {
  padding: 0 32px 32px;
}

/* 品牌区域 */
.brand-area {
  text-align: center;
  margin-bottom: 32px;
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

.register-logo {
  background: linear-gradient(135deg, #3b82f6, #06b6d4, #14b8a6);
  box-shadow: 0 8px 24px rgba(59, 130, 246, 0.3);
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

.forgot-link {
  display: flex;
  align-items: center;
  gap: 4px;
  font-size: 14px;
  color: #10b981;
  font-weight: 500;
  text-decoration: none;
  transition: all 0.3s ease;
}

.forgot-link:hover {
  color: #059669;
  gap: 8px;
}

.link-icon {
  transition: transform 0.3s ease;
}

.forgot-link:hover .link-icon {
  transform: translateX(4px);
}

/* 提交按钮 */
.submit-item {
  margin-bottom: 0;
  margin-top: 8px;
}

.submit-btn {
  width: 100%;
  height: 56px !important;
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

.register-btn {
  background: linear-gradient(135deg, #3b82f6, #06b6d4, #14b8a6) !important;
  box-shadow: 0 6px 20px rgba(59, 130, 246, 0.35) !important;
}

.register-btn:hover {
  background: linear-gradient(135deg, #2563eb, #0891b2, #0d9488) !important;
  box-shadow: 0 8px 24px rgba(59, 130, 246, 0.45) !important;
}

.btn-icon {
  transition: transform 0.3s ease;
}

.submit-btn:hover .btn-icon {
  transform: translateX(4px);
}

/* 同意条款 */
.agreement-item {
  margin-bottom: 0;
}

.agreement-label {
  display: flex;
  align-items: flex-start;
  gap: 6px;
  font-size: 12px;
  color: #9ca3af;
  line-height: 1.6;
  flex-wrap: wrap;
}

.agreement-link {
  color: #10b981;
  text-decoration: none;
  font-weight: 500;
  transition: color 0.3s ease;
}

.agreement-link:hover {
  color: #059669;
}

/* 社交登录 */
.social-login {
  margin-top: 16px;
  padding-top: 24px;
  border-top: 1px solid rgba(255, 255, 255, 0.1);
}

.social-divider {
  text-align: center;
  margin-bottom: 16px;
}

.social-divider span {
  font-size: 12px;
  color: #9ca3af;
}

.social-btns {
  display: flex;
  gap: 12px;
}

.social-btn {
  flex: 1;
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 8px;
  height: 48px;
  border: 1px solid rgba(255, 255, 255, 0.15);
  background: rgba(255, 255, 255, 0.05);
  border-radius: 12px;
  cursor: pointer;
  transition: all 0.3s ease;
  font-size: 14px;
  font-weight: 500;
  color: #d1d5db;
}

.social-btn:hover {
  border-color: rgba(16, 185, 129, 0.4);
  background: rgba(16, 185, 129, 0.1);
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.2);
  transform: translateY(-2px);
}

.social-icon {
  width: 18px;
  height: 18px;
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
