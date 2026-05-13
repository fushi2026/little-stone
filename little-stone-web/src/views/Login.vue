<template>
  <div class="min-h-screen w-full flex items-center justify-center bg-gradient-to-br from-blue-600 via-purple-600 to-indigo-700">
    <!-- 背景装饰 -->
    <div class="absolute inset-0 overflow-hidden">
      <div class="absolute -top-40 -right-40 w-80 h-80 bg-white/10 rounded-full blur-3xl"></div>
      <div class="absolute -bottom-40 -left-40 w-80 h-80 bg-purple-400/10 rounded-full blur-3xl"></div>
    </div>

    <div class="relative w-full max-w-md mx-4">
      <!-- 卡片容器 -->
      <div class="bg-white/95 backdrop-blur-lg rounded-2xl shadow-2xl overflow-hidden">
        <!-- 标签切换 -->
        <div class="flex border-b border-gray-100">
          <button
            v-for="tab in tabs"
            :key="tab.key"
            class="flex-1 py-4 px-6 text-sm font-medium transition-colors relative"
            :class="activeTab === tab.key ? 'text-blue-600' : 'text-gray-500 hover:text-gray-700'"
            @click="activeTab = tab.key"
          >
            {{ tab.label }}
            <div
              v-if="activeTab === tab.key"
              class="absolute bottom-0 left-1/2 -translate-x-1/2 w-12 h-0.5 bg-blue-600 rounded-full"
            ></div>
          </button>
        </div>

        <!-- 登录表单 -->
        <div v-show="activeTab === 'login'" class="p-6 space-y-5">
          <div class="text-center mb-2">
            <h3 class="text-xl font-semibold text-gray-800">欢迎回来</h3>
            <p class="text-sm text-gray-500 mt-1">请登录您的账号</p>
          </div>

          <el-form
            ref="loginFormRef"
            :model="loginForm"
            :rules="loginRules"
            class="space-y-4"
            @submit.prevent="handleLogin"
          >
            <el-form-item prop="username">
              <el-input
                v-model="loginForm.username"
                placeholder="请输入手机号或邮箱"
                prefix-icon="User"
                clearable
                class="h-11"
              />
            </el-form-item>

            <el-form-item prop="password">
              <el-input
                v-model="loginForm.password"
                type="password"
                placeholder="请输入密码"
                prefix-icon="Lock"
                clearable
                show-password
                class="h-11"
              />
            </el-form-item>

            <div class="flex items-center justify-between text-sm">
              <label class="flex items-center cursor-pointer">
                <el-checkbox v-model="loginForm.remember" border class="mr-2" />
                <span class="text-gray-600">记住我</span>
              </label>
              <a href="#" class="text-blue-600 hover:text-blue-700 transition-colors">
                忘记密码？
              </a>
            </div>

            <el-form-item>
              <el-button
                type="primary"
                class="w-full h-11 text-base font-medium"
                :loading="isLoading"
                @click="handleLogin"
              >
                登 录
              </el-button>
            </el-form-item>
          </el-form>

          <!-- 社交登录 -->
          <div class="relative my-4">
            <div class="absolute inset-0 flex items-center">
              <div class="w-full border-t border-gray-200"></div>
            </div>
            <div class="relative flex justify-center text-sm">
              <span class="px-4 bg-white text-gray-500">或</span>
            </div>
          </div>

          <div class="grid grid-cols-2 gap-3">
            <el-button
              type="default"
              class="h-10 border-gray-200 hover:bg-gray-50 flex items-center justify-center gap-2"
            >
              <svg class="w-5 h-5" viewBox="0 0 24 24">
                <path fill="#4285F4" d="M22.56 12.25c0-.78-.07-1.53-.2-2.25H12v4.26h5.92c-.26 1.37-1.04 2.53-2.21 3.31v2.77h3.57c2.08-1.92 3.28-4.74 3.28-8.09z"/>
                <path fill="#34A853" d="M12 23c2.97 0 5.46-.98 7.28-2.66l-3.57-2.77c-.98.66-2.23 1.06-3.71 1.06-2.86 0-5.29-1.93-6.16-4.53H2.18v2.84C3.99 20.53 7.7 23 12 23z"/>
                <path fill="#FBBC05" d="M5.84 14.09c-.22-.66-.35-1.36-.35-2.09s.13-1.43.35-2.09V7.07H2.18C1.43 8.55 1 10.22 1 12s.43 3.45 1.18 4.93l2.85-2.22.81-.62z"/>
                <path fill="#EA4335" d="M12 5.38c1.62 0 3.06.56 4.21 1.64l3.15-3.15C17.45 2.09 14.97 1 12 1 7.7 1 3.99 3.47 2.18 7.07l3.66 2.84c.87-2.6 3.3-4.53 6.16-4.53z"/>
              </svg>
              <span class="text-gray-700">Google</span>
            </el-button>
            <el-button
              type="default"
              class="h-10 border-gray-200 hover:bg-gray-50 flex items-center justify-center gap-2"
            >
              <svg class="w-5 h-5" viewBox="0 0 24 24" fill="#07C160">
                <path d="M8.691 2.188C3.891 2.188 0 5.476 0 9.53c0 2.212 1.17 4.203 3.002 5.55a.59.59 0 0 1 .213.665l-.39 1.48c-.019.07-.048.141-.048.213 0 .163.13.295.29.295a.326.326 0 0 0 .167-.054l1.903-1.114a.864.864 0 0 1 .717-.098 10.16 10.16 0 0 0 2.837.403c4.801 0 8.692-3.287 8.692-7.342 0-4.054-3.891-7.334-8.692-7.334z"/>
              </svg>
              <span class="text-gray-700">微信</span>
            </el-button>
          </div>
        </div>

        <!-- 注册表单 -->
        <div v-show="activeTab === 'register'" class="p-6 space-y-5">
          <div class="text-center mb-2">
            <h3 class="text-xl font-semibold text-gray-800">创建账号</h3>
            <p class="text-sm text-gray-500 mt-1">开启您的旅程</p>
          </div>

          <el-form
            ref="registerFormRef"
            :model="registerForm"
            :rules="registerRules"
            class="space-y-4"
            @submit.prevent="handleRegister"
          >
            <el-form-item prop="username">
              <el-input
                v-model="registerForm.username"
                placeholder="请输入用户名"
                prefix-icon="User"
                clearable
                class="h-11"
              />
            </el-form-item>

            <el-form-item prop="email">
              <el-input
                v-model="registerForm.email"
                placeholder="请输入邮箱"
                prefix-icon="Mail"
                clearable
                class="h-11"
              />
            </el-form-item>

            <el-form-item prop="phone">
              <el-input
                v-model="registerForm.phone"
                placeholder="请输入手机号"
                prefix-icon="Phone"
                clearable
                class="h-11"
              />
            </el-form-item>

            <el-form-item prop="password">
              <el-input
                v-model="registerForm.password"
                type="password"
                placeholder="请输入密码"
                prefix-icon="Lock"
                clearable
                show-password
                class="h-11"
              />
            </el-form-item>

            <el-form-item prop="confirmPassword">
              <el-input
                v-model="registerForm.confirmPassword"
                type="password"
                placeholder="请确认密码"
                prefix-icon="Lock"
                clearable
                show-password
                class="h-11"
              />
            </el-form-item>

            <el-form-item>
              <label class="flex items-start cursor-pointer">
                <el-checkbox v-model="registerForm.agreed" border class="mt-0.5 mr-2" />
                <span class="text-xs text-gray-500">
                  我已阅读并同意
                  <a href="#" class="text-blue-600 hover:underline">服务条款</a>
                  和
                  <a href="#" class="text-blue-600 hover:underline">隐私政策</a>
                </span>
              </label>
            </el-form-item>

            <el-form-item>
              <el-button
                type="primary"
                class="w-full h-11 text-base font-medium"
                :loading="isLoading"
                @click="handleRegister"
              >
                注 册
              </el-button>
            </el-form-item>
          </el-form>
        </div>
      </div>

      <!-- 底部提示 -->
      <p class="text-center text-white/80 text-sm mt-6">
        已有账号？
        <a
          v-if="activeTab === 'register'"
          href="#"
          class="font-medium hover:text-white transition-colors"
          @click.prevent="activeTab = 'login'"
        >
           立即登录
        </a>
        <a
          v-else
          href="#"
          class="font-medium hover:text-white transition-colors"
          @click.prevent="activeTab = 'register'"
        >
          立即注册
        </a>
      </p>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive } from 'vue'
import { ElMessage, ElForm } from 'element-plus'
import { useUserStore } from '@/stores/user'
import { useRouter } from 'vue-router'

const router = useRouter()
const userStore = useUserStore()

// 标签配置
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

// 登录验证规则
const loginRules = reactive({
  username: [
    { required: true, message: '请输入账号', trigger: 'blur' },
    { min: 3, max: 20, message: '账号长度在3-20个字符之间', trigger: 'blur' }
  ],
  password: [
    { required: true, message: '请输入密码', trigger: 'blur' },
    { min: 6, max: 32, message: '密码长度在6-32个字符之间', trigger: 'blur' }
  ]
})

// 注册验证规则
const registerRules: Record<string, any> = {
  username: [
    { required: true, message: '请输入用户名', trigger: 'blur' },
    { min: 3, max: 20, message: '用户名长度在3-20个字符之间', trigger: 'blur' },
    { pattern: /^[a-zA-Z0-9_]+$/, message: '用户名只能包含字母、数字和下划线', trigger: 'blur' }
  ],
  email: [
    { required: true, message: '请输入邮箱', trigger: 'blur' },
    { type: 'email' as const, message: '请输入有效的邮箱地址', trigger: 'blur' }
  ],
  phone: [
    { required: true, message: '请输入手机号', trigger: 'blur' },
    { pattern: /^1[3-9]\d{9}$/, message: '请输入有效的手机号', trigger: 'blur' }
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
  ],
  agreed: [
    {
      validator: (_rule: any, value: boolean, callback: any) => {
        if (!value) {
          callback(new Error('请同意服务条款和隐私政策'))
        } else {
          callback()
        }
      },
      trigger: 'change'
    }
  ]
}

// 登录处理
const handleLogin = async () => {
  try {
    //await loginFormRef.value?.validate()
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
  try {
    await registerFormRef.value?.validate()
  } catch {
    return
  }

  isLoading.value = true

  try {
    // 模拟注册请求
    await new Promise(resolve => setTimeout(resolve, 1000))
    ElMessage.success('注册成功！')
    activeTab.value = 'login'
    // 重置注册表单
    registerFormRef.value?.resetFields()
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
/* 自定义滚动条 */
::-webkit-scrollbar {
  width: 6px;
}

::-webkit-scrollbar-track {
  background: transparent;
}

::-webkit-scrollbar-thumb {
  background: #d1d5db;
  border-radius: 3px;
}

::-webkit-scrollbar-thumb:hover {
  background: #9ca3af;
}
</style>
