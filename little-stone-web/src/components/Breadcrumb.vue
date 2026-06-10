<template>
    <div class="breadcrumb-container">
        <el-breadcrumb separator="/">
            <el-breadcrumb-item :to="{ path: '/' }">主页</el-breadcrumb-item>
            <el-breadcrumb-item v-for="(item, index) in breadcrumbList" :key="index">
                <router-link v-if="index < breadcrumbList.length - 1" :to="item.path">
                    {{ item.title }}
                </router-link>
                <span v-else>{{ item.title }}</span>
            </el-breadcrumb-item>
        </el-breadcrumb>
    </div>
</template>

<script setup lang="ts">
import { ref, watch } from 'vue'
import { useRoute } from 'vue-router'
import { useUserStore } from '@/stores/user'
import type { MenuItem } from '@/types/auth'

const route = useRoute()
const userStore = useUserStore()

interface BreadcrumbItem {
    title: string
    path: string
}

const breadcrumbList = ref<BreadcrumbItem[]>([])

/**
 * 规范化路径，去除 /api 前缀
 */
const normalizePath = (path: string): string => {
    if (path.startsWith('/api')) {
        return path.replace('/api', '')
    }
    return path.startsWith('/') ? path : `/${path}`
}

/**
 * 拼接路径
 */
const joinPath = (parentPath: string, childPath: string): string => {
    const parent = normalizePath(parentPath)
    const child = normalizePath(childPath)
    
    if (parent === '/') {
        return child
    }
    
    return `${parent}${child.startsWith('/') ? '' : '/'}${child}`
}

const findMenuPath = (
    menus: MenuItem[],
    currentPath: BreadcrumbItem[] = []
): BreadcrumbItem[] => {
    for (const menu of menus) {
        let menuFullPath: string
        if (currentPath.length > 0) {
            const parentPath = currentPath[currentPath.length - 1]?.path || '/'
            menuFullPath = joinPath(parentPath, menu.path)
        } else {
            menuFullPath = normalizePath(menu.path)
        }

        const newPath = [...currentPath, { title: menu.menuName, path: menuFullPath }]

        // 检查当前路径是否匹配
        if (route.path === menuFullPath) {
            return newPath
        }

        if (menu.children && menu.children.length > 0) {
            const childResult = findMenuPath(menu.children, newPath)
            if (childResult.length > 0) {
                return childResult
            }
        }
    }
    return []
}

const updateBreadcrumb = () => {
    if (route.path === '/' || route.path === '/home') {
        breadcrumbList.value = []
        return
    }

    const pathList = findMenuPath(userStore.menuList || [])
    breadcrumbList.value = pathList
}

// 监听路由变化
watch(() => route.path, () => {
    updateBreadcrumb()
}, { immediate: true })

// 监听菜单数据变化
watch(() => userStore.menuList, () => {
    updateBreadcrumb()
}, { deep: true, immediate: true })

</script>

<style scoped>
    .breadcrumb-container {
        height: 40px;
        padding: 10px 20px;
    }
</style>