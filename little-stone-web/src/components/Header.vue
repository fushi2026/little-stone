<template>
    <div class="w-full h-full flex items-center">
        <div 
            class="logo-title-container w-[200px] flex items-center justify-center transition-all duration-300"
            @click="$router.push('/')"
        >
            <img src="@/assets/ico.svg" class="h-8 w-8" />
            <span class="logo-title flex items-center ml-2">小石头在线</span>
        </div>

        <!-- 左侧模块菜单 -->
        <ul class="flex-grow flex items-center space-x-1 ml-5 text-gray-600 dark:text-gray-300">
            <li 
             v-for="(item, index) in userStore.moduleList" 
             :key="item.moduleCode" 
             class="px-3 py-1.5 rounded-lg cursor-pointer flex items-center justify-center space-x-1.5 transition-all duration-200"
             :class="getModuleItemClass(item.moduleCode)"
             @click="selectModule(item.moduleCode, index)"
             >
                <el-icon class="w-4 h-4"><DynamicIcon :icon="item.icon" /></el-icon>
                <span class="text-sm font-medium">{{ item.moduleName }}</span>
            </li>
        </ul>

        <!-- 右侧操作区 -->
        <div class="flex items-center space-x-3">
            <!-- 深色模式切换 -->
            <button
                @click="toggleDarkMode"
                class="p-2 rounded-full hover:bg-gray-100 dark:hover:bg-gray-800 transition-all duration-200 hover:scale-105"
                :title="isDarkMode ? '切换到浅色模式' : '切换到深色模式'"
            >
                <svg
                    v-if="!isDarkMode"
                    class="w-5 h-5 text-gray-600 dark:text-gray-400"
                    fill="none"
                    stroke="currentColor"
                    viewBox="0 0 24 24"
                    xmlns="http://www.w3.org/2000/svg"
                >
                    <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M12 3v1m0 16v1m9-9h-1M4 12H3m15.364 6.364l-.707-.707M6.343 6.343l-.707-.707m12.728 0l-.707.707M6.343 17.657l-.707.707M16 12a4 4 0 11-8 0 4 4 0 018 0z"></path>
                </svg>
                <svg
                    v-else
                    class="w-5 h-5 text-gray-400 dark:text-gray-300"
                    fill="none"
                    stroke="currentColor"
                    viewBox="0 0 24 24"
                    xmlns="http://www.w3.org/2000/svg"
                >
                    <path
                        stroke-linecap="round"
                        stroke-linejoin="round"
                        stroke-width="2"
                        d="M20.354 15.354A9 9 0 018.646 3.646 9.003 9.003 0 0012 21a9.003 9.003 0 008.354-5.646z"
                    ></path>
                </svg>
            </button>

            <!-- 用户信息下拉菜单 -->
            <div class="relative user-dropdown-container">
                <div 
                    class="flex items-center space-x-2.5 px-3 py-2 rounded-xl hover:bg-gray-50 dark:hover:bg-gray-800/50 cursor-pointer transition-all duration-200 hover:shadow-md"
                    @click="toggleDropdown"
                >
                    <div class="relative">
                        <img 
                            src="@/assets/img/happyhouse.svg" 
                            class="h-9 w-9 rounded-full border-2 border-gray-200 dark:border-gray-700 object-cover transition-transform duration-200 hover:scale-105" 
                        />
                        <span class="absolute bottom-0 right-0 w-3 h-3 bg-green-500 border-2 border-white dark:border-gray-900 rounded-full"></span>
                    </div>
                    <div class="text-left">
                        <span class="text-sm font-semibold text-gray-800 dark:text-gray-100">{{ userStore.userInfo?.username || 'admin' }}</span>
                        <p class="text-xs text-gray-500 dark:text-gray-400">系统管理员</p>
                    </div>
                    <el-icon class="w-4 h-4 text-gray-400 transition-transform duration-200" :class="{ 'rotate-180': isDropdownOpen }">
                        <ChevronDown />
                    </el-icon>
                </div>

                <!-- 下拉面板 -->
                <Transition name="dropdown">
                    <div 
                        v-if="isDropdownOpen" 
                        class="custom-dropdown-menu"
                        @click.stop
                    >
                        <div class="menu-section">
                            <div class="menu-item disabled">
                                <el-icon class="w-4 h-4 mr-2.5 text-gray-400">
                                    <User />
                                </el-icon>
                                <span class="text-sm text-gray-400">个人中心</span>
                            </div>
                            <div class="menu-item" @click="handleChangePasswordClick">
                                <el-icon class="w-4 h-4 mr-2.5">
                                    <Lock />
                                </el-icon>
                                <span class="text-sm">修改密码</span>
                            </div>
                        </div>
                        <div class="menu-divider"></div>
                        <div class="menu-section">
                            <div class="menu-item logout-item" @click="handleLogoutClick">
                                <el-icon class="w-4 h-4 mr-2.5 text-red-400">
                                    <Logout />
                                </el-icon>
                                <span class="text-sm text-red-500">退出登录</span>
                            </div>
                        </div>
                    </div>
                </Transition>
            </div>
        </div>
    </div>

    <!-- 修改密码对话框 -->
    <el-dialog
        v-model="changePasswordDialogVisible"
        title="修改密码"
        width="420px"
        :close-on-click-modal="false"
        destroy-on-close
    >
        <el-form
            ref="changePasswordFormRef"
            :model="changePasswordForm"
            :rules="changePasswordRules"
            label-width="80px"
        >
            <el-form-item label="旧密码" prop="oldPassword">
                <el-input
                    v-model="changePasswordForm.oldPassword"
                    type="password"
                    placeholder="请输入旧密码"
                    show-password
                    autocomplete="new-password"
                />
            </el-form-item>
            <el-form-item label="新密码" prop="newPassword">
                <el-input
                    v-model="changePasswordForm.newPassword"
                    type="password"
                    placeholder="请输入新密码（至少6位）"
                    show-password
                    autocomplete="new-password"
                />
            </el-form-item>
            <el-form-item label="确认密码" prop="confirmPassword">
                <el-input
                    v-model="changePasswordForm.confirmPassword"
                    type="password"
                    placeholder="请再次输入新密码"
                    show-password
                    autocomplete="new-password"
                />
            </el-form-item>
        </el-form>
        <template #footer>
            <el-button @click="changePasswordDialogVisible = false">取消</el-button>
            <el-button type="primary" :loading="changePasswordLoading" @click="handleChangePassword">
                确认修改
            </el-button>
        </template>
    </el-dialog>
</template>

<script setup lang="ts">
import { ref, onMounted, onUnmounted } from 'vue'
import { useUserStore } from '@/stores/user'
import DynamicIcon from './DynamicIcon.vue'
import { ElMessageBox, ElMessage, type FormInstance, type FormRules } from 'element-plus'
import { changePassword as changePasswordApi } from '@/api/user'

const userStore = useUserStore()
const isDarkMode = ref(false)
const isDropdownOpen = ref(false)
const selectedModuleCode = ref<string>('')

// 修改密码相关
const changePasswordDialogVisible = ref(false)
const changePasswordLoading = ref(false)
const changePasswordFormRef = ref<FormInstance>()
const changePasswordForm = ref({
    oldPassword: '',
    newPassword: '',
    confirmPassword: ''
})

// 表单验证规则
const validateConfirmPassword = (rule: any, value: any, callback: any) => {
    if (value === '') {
        callback(new Error('请再次输入新密码'))
    } else if (value !== changePasswordForm.value.newPassword) {
        callback(new Error('两次输入的密码不一致'))
    } else {
        callback()
    }
}

const changePasswordRules: FormRules = {
    oldPassword: [
        { required: true, message: '请输入旧密码', trigger: 'blur' }
    ],
    newPassword: [
        { required: true, message: '请输入新密码', trigger: 'blur' },
        { min: 6, message: '密码长度至少6位', trigger: 'blur' }
    ],
    confirmPassword: [
        { required: true, validator: validateConfirmPassword, trigger: 'blur' }
    ]
}

const toggleDarkMode = () => {
    isDarkMode.value = !isDarkMode.value
    const htmlElement = document.documentElement
    if (htmlElement.classList.contains('dark')) {
        htmlElement.classList.remove('dark')
        localStorage.setItem('theme', 'light')
    } else {
        htmlElement.classList.add('dark')
        localStorage.setItem('theme', 'dark')
    }
}

const getModuleItemClass = (moduleCode: string) => {
    const isSelected = selectedModuleCode.value === moduleCode
    if (isSelected) {
        return 'bg-green-50 text-green-700 dark:bg-green-900/30 dark:text-green-400 shadow-sm'
    }
    return 'hover:bg-green-50/50 hover:text-green-700 dark:hover:bg-green-900/20 dark:hover:text-green-400'
}

const selectModule = (moduleCode: string, index: number) => {
    selectedModuleCode.value = moduleCode
    console.log(`Selected module: ${moduleCode} (index: ${index})`)
}

const initDefaultModule = () => {
    if (userStore.moduleList.length > 0) {
        selectedModuleCode.value = userStore.moduleList[0]?.moduleCode || ''
    }
}

const toggleDropdown = () => {
    isDropdownOpen.value = !isDropdownOpen.value
}

const closeDropdown = () => {
    isDropdownOpen.value = false
}

const handleChangePasswordClick = () => {
    // 先关闭下拉面板
    closeDropdown()
    // 重置表单
    changePasswordForm.value = {
        oldPassword: '',
        newPassword: '',
        confirmPassword: ''
    }
    changePasswordFormRef.value?.resetFields()
    // 打开对话框
    changePasswordDialogVisible.value = true
}

const handleChangePassword = async () => {
    if (!changePasswordFormRef.value) return
    
    await changePasswordFormRef.value.validate(async (valid) => {
        if (valid) {
            changePasswordLoading.value = true
            try {
                await changePasswordApi(
                    changePasswordForm.value.oldPassword,
                    changePasswordForm.value.newPassword
                )
                ElMessage.success('密码修改成功，请重新登录')
                changePasswordDialogVisible.value = false
                // 退出登录，让用户重新登录
                userStore.logout()
            } catch (error: any) {
                ElMessage.error(error.message || '密码修改失败')
            } finally {
                changePasswordLoading.value = false
            }
        }
    })
}

const handleLogoutClick = async () => {
    // 先关闭下拉面板
    closeDropdown()
    
    try {
        await ElMessageBox.confirm(
            '确定要退出登录吗？',
            '提示',
            {
                confirmButtonText: '确定退出',
                cancelButtonText: '取消',
                type: 'warning'
            }
        )
        userStore.logout()
        ElMessage.success('已安全退出')
    } catch {
        ElMessage.info('已取消退出')
    }
}

// 点击页面其他地方关闭下拉面板
const handleClickOutside = (event: MouseEvent) => {
    const target = event.target as HTMLElement
    if (!target.closest('.user-dropdown-container')) {
        closeDropdown()
    }
}

onMounted(() => {
    const savedTheme = localStorage.getItem('theme')
    isDarkMode.value = savedTheme === 'dark'
    
    document.addEventListener('click', handleClickOutside)
    
    // 初始化默认选中第一个模块
    initDefaultModule()
})

onUnmounted(() => {
    document.removeEventListener('click', handleClickOutside)
})
</script>

<style type="scss" scoped>
.logo-title-container {
  cursor: pointer;
  .logo-title {
    background: -webkit-linear-gradient(315deg, #42d392 25%, #647eff);
    background-clip: text;
    -webkit-background-clip: text;
    -webkit-text-fill-color: transparent;
    letter-spacing: 3px;
    font-family: "PingFang SC", sans-serif;
    font-size: 20px;
    font-weight: 600;
    text-rendering: optimizeLegibility;
  }
}

.custom-dropdown-menu {
    position: absolute;
    top: calc(100% + 8px);
    right: 0;
    background: #ffffff;
    border-radius: 12px;
    box-shadow: 0 10px 40px rgba(0, 0, 0, 0.15), 0 2px 10px rgba(0, 0, 0, 0.08);
    padding: 8px 0;
    min-width: 170px;
    overflow: hidden;
    z-index: 1000;
}

.menu-section {
    padding: 0 8px;
}

.menu-item {
    display: flex;
    align-items: center;
    padding: 10px 14px;
    margin: 0 4px;
    border-radius: 8px;
    cursor: pointer;
    transition: all 0.15s ease;
    color: #374151;
}

.menu-item:hover:not(.disabled) {
    background: #f3f4f6;
    transform: translateX(4px);
}

.menu-item.disabled {
    cursor: not-allowed;
}

.menu-item.logout-item:hover {
    background: #fee2e2;
}

.menu-divider {
    height: 1px;
    background: #e5e7eb;
    margin: 8px 0;
}

.rotate-180 {
    transform: rotate(180deg);
}

/* 下拉动画 */
.dropdown-enter-active,
.dropdown-leave-active {
    transition: all 0.2s ease;
}

.dropdown-enter-from,
.dropdown-leave-to {
    opacity: 0;
    transform: translateY(-8px) scale(0.95);
}

/* 深色模式 */
.dark .custom-dropdown-menu {
    background: #1f2937;
    box-shadow: 0 10px 40px rgba(0, 0, 0, 0.3), 0 2px 10px rgba(0, 0, 0, 0.2);
}

.dark .menu-item {
    color: #e5e7eb;
}

.dark .menu-item:hover:not(.disabled) {
    background: #374151;
}

.dark .menu-item.logout-item:hover {
    background: rgba(239, 68, 68, 0.15);
}

.dark .menu-divider {
    background: #374151;
}
</style>