<template>
  <div class="login-container h-screen w-full flex items-center justify-center bg-gradient-to-r from-sky-500 to-indigo-500">
    <div class="login-card w-[460px] border-shadow-lg p-8 rounded-lg bg-white/90 backdrop-blur-md flex flex-col">
      <div class="login-header mb-5">
        <h2 class="text-gray-600 font-sans text-2xl tracking-wider">系统登录</h2>
      </div>
      <el-form
        ref="loginFormRef"
        :model="loginForm"
        :rules="loginRules"
        class="login-form flex flex-col gap-2"
        @submit.prevent="handleLogin"
      >
        <el-form-item prop="username">
          <el-input
            v-model="loginForm.username"
            placeholder="请输入账号"
            prefix-icon="User"
            clearable
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
          />
        </el-form-item>
        
        <el-form-item class="h-5 flex items-center justify-center" prop="remember">
          <el-checkbox v-model="loginForm.remember">记住我</el-checkbox>
        </el-form-item>
        
        <el-form-item class="login-btn-group">
          <el-button
            type="primary"
            class="login-btn"
            :loading="isLoading"
            @click="handleLogin"
          >
            登录
          </el-button>
          <el-button
            class="reset-btn"
            @click="resetForm"
          >
            重置
          </el-button>
        </el-form-item>
      </el-form>
    </div>
  </div>
</template>

<script setup type="ts">
import { ref, reactive } from 'vue'
import { ElMessage } from 'element-plus'
import { useUserStore } from '@/stores/user'
import { useRouter } from 'vue-router'

const router = useRouter()

// 表单引用
const loginFormRef = ref(null)

// 加载状态
const isLoading = ref(false)

// 登录表单数据
const loginForm = reactive({
  username: '', // 账号
  password: '', // 密码
  remember: false // 记住我
})

// 表单验证规则
const loginRules = reactive({
  username: [
    { required: true, message: '请输入账号', trigger: 'blur' }
  ],
  password: [
    { required: true, message: '请输入密码', trigger: 'blur' }
  ]
})

// 登录处理函数
const handleLogin = async () => {
  // 表单验证
  try {
    await loginFormRef.value.validate()
  } catch (error) {
    // 验证失败直接返回
    return
  }

  // 模拟登录加载
  isLoading.value = true

  try {
    //login(loginForm)
    const store = useUserStore()

    store.login(loginForm)

    ElMessage.success('登录成功！')
    
    router.push('/finance/dashboard')
  } catch (error) {
    // 登录失败提示
    ElMessage.error('登录失败：账号或密码错误')
    console.error('登录错误：', error)
  } finally {
    // 结束加载状态
    isLoading.value = false
  }
}

// 重置表单
const resetForm = () => {
  loginFormRef.value.resetFields()
}
</script>

<style scoped type="scss">
.login-container {
    background: url('@/assets/img/7cd5837b89f3a6e237ff9daf21082f04.jpg') no-repeat center center / cover;
}
/* 响应式适配 */
@media (max-width: 450px) {
  .login-card {
    width: 90%;
    padding: 20px;
  }
}
</style>