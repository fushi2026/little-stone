<template>
    <div class="breadcrumb-container">
        <el-breadcrumb separator="/">
            <el-breadcrumb-item :to="{ path: '/' }">主页</el-breadcrumb-item>
            <el-breadcrumb-item 
                v-for="(item, index) in breadcrumbList" 
                :key="index"
                :to="!item.isParent ? item.path : undefined"
                :disabled="item.isParent"
            >
                {{ item.title }}
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
    isParent?: boolean
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

        const isParent = menu.children && menu.children.length > 0
        const newPath = [...currentPath, { title: menu.menuName, path: menuFullPath, isParent }]

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

/* ========== 浅色模式 ========== */
:deep(.el-breadcrumb__item) {
    color: #6b7280;
}

:deep(.el-breadcrumb__inner) {
    color: #6b7280;
    font-weight: 400;
    transition: color 0.2s ease;
}

:deep(.el-breadcrumb__inner.is-link:hover) {
    color: #42d392;
}

/* 最后一项（当前页） */
:deep(.el-breadcrumb__item:last-child .el-breadcrumb__inner) {
    color: #1f2937;
    font-weight: 600;
}

/* 分隔符 */
:deep(.el-breadcrumb__separator) {
    color: #cbd5e1;
}

/* 面包屑链接 hover */
:deep(.el-breadcrumb__inner.is-link:hover) {
    color: #42d392;
}

/* 禁用项（不可点击的分类） */
:deep(.el-breadcrumb__item.is-disabled .el-breadcrumb__inner) {
    color: #9ca3af;
    cursor: default;
}

:deep(.el-breadcrumb__item.is-disabled .el-breadcrumb__inner:hover) {
    color: #9ca3af;
}
</style>

<!-- 暗黑模式：用非 scoped 样式，确保 .dark 能匹配到 html 上的类 -->
<style>
/* 深色模式 - 面包屑文字 */
.dark .breadcrumb-container .el-breadcrumb__inner {
    color: #d1d5db !important;
}

/* 深色模式 - 最后一项 */
.dark .breadcrumb-container .el-breadcrumb__item:last-child .el-breadcrumb__inner {
    color: #f3f4f6 !important;
    font-weight: 600;
}

/* 深色模式 - 分隔符 */
.dark .breadcrumb-container .el-breadcrumb__separator {
    color: #6b7280 !important;
}

/* 深色模式 - 链接 hover */
.dark .breadcrumb-container .el-breadcrumb__inner.is-link:hover {
    color: #42d392 !important;
}

/* 深色模式 - 禁用项 */
.dark .breadcrumb-container .el-breadcrumb__item.is-disabled .el-breadcrumb__inner {
    color: #9ca3af !important;
}

.dark .breadcrumb-container .el-breadcrumb__item.is-disabled .el-breadcrumb__inner:hover {
    color: #9ca3af !important;
}
</style>