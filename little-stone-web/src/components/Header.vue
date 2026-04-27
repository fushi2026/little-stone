<template>
    <div class="w-full px-4 flex items-center">
        <ul class="flex-grow flex space-x-6 ml-5 text-gray-600 dark:text-gray-300">
            <li class="w-[86px] hover:cursor-pointer hover:text-green-700 flex items-center justify-around"><el-icon><Suitcase /></el-icon>业务管理</li>
            <li class="w-[86px] hover:cursor-pointer hover:text-green-700 flex items-center justify-around"><el-icon><Coin /></el-icon>财务管理</li>
            <li class="w-[86px] hover:cursor-pointer hover:text-green-700 flex items-center justify-around"><el-icon><PieChart /></el-icon>统计报表</li>
            <li class="w-[86px] hover:cursor-pointer hover:text-green-700 flex items-center justify-around"><el-icon><Setting /></el-icon>系统设置</li>
        </ul>
        <div class="w-[200px] flex items-center justify-around">
            <div class="flex items-center">
                <button
                    @click="toggleDarkMode"
                    class="p2 rounded-full"
                >
                    <svg
                        v-if="!isDarkMode"
                        class="w-6 h-6 text-gray-600"
                        fill="none"
                        stroke="currentColor"
                        viewBox="0 0 24 24"
                        xmlns="http://www.w3.org/2000/svg"
                    >
                        <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M12 3v1m0 16v1m9-9h-1M4 12H3m15.364 6.364l-.707-.707M6.343 6.343l-.707-.707m12.728 0l-.707.707M6.343 17.657l-.707.707M16 12a4 4 0 11-8 0 4 4 0 018 0z"></path>
                    </svg>
                    <svg
                        v-else
                        class="w-6 h-6 text-gray-300"
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
            </div>
            <div class="user-container w-[90px] flex items-center hover:cursor-pointer">
                <img src="@/assets/img/happyhouse.svg" class="h-8 w-8 rounded-sm mr-2" />
                <span class="text-bold text-gray-600 dark:text-gray-300">admin</span>
            </div>
        </div>
    </div>
</template>

<script setup lang="ts">
import { ref, onMounted } from 'vue';

var isDarkMode = ref(false);

var toggleDarkMode = () => {
    isDarkMode.value = !isDarkMode.value;
    const htmlElement = document.documentElement;
    if (htmlElement.classList.contains('dark')) {
        htmlElement.classList.remove('dark');
        localStorage.setItem('theme', 'light');
    } else {
        htmlElement.classList.add('dark');
        localStorage.setItem('theme', 'dark');
    }
}

// 页面加载时读取本地存储的主题
onMounted(() => {
    const savedTheme = localStorage.getItem('theme')
    isDarkMode.value = savedTheme === 'dark'
})
</script>

<style scoped>
.user-container {
    &:hover {
        opacity: 0.8;
        span {
            color: #15803d; /* Tailwind's green-700 */
        }
    }
}
</style>